// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import com.querydsl.sql.postgresql.PostgreSQLQueryFactory;
import fi.hsl.parkandride.core.domain.Lock;
import fi.hsl.parkandride.core.domain.LockAcquireFailedException;
import fi.hsl.parkandride.core.domain.LockException;
import fi.hsl.parkandride.core.service.ValidationService;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

// NOTE: class not marked with @Transactional to allow some tests to run
// non-transactionally (and allow the tests to create the transactions within
// separate threads -> thread pool with max 2 connections is enough).
// Those test methods that require transactionality (those that do not run
// operations on LockDao in a separate thread), are annotated with
// @Transactional to have a transaction in test method scope.
public class LockDaoTest extends AbstractDaoTest {

    private static final String LOCK_OWNER_NAME = "test-lock-owner";
    private static final String TEST_LOCK_NAME = "test-lock";
    private static final Duration TEST_LOCK_DURATION = Duration.standardSeconds(10);

    @Inject
    private PostgreSQLQueryFactory queryFactory;

    @Inject
    private ValidationService validationService;

    @Inject
    private PlatformTransactionManager transactionManager;

    private LockDao lockDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void createLockDao() {
        lockDao = new LockDao(queryFactory, validationService, LOCK_OWNER_NAME);
    }

    @Before
    public void clean_database_so_that_test_do_not_need_to_release_locks() {
        cleanup();
    }

    @Test
    @Transactional
    public void lock_acquisition_creates_a_lock_in_database_and_releasing_deletes_it() {
        Lock lock = lockDao.acquireLock(TEST_LOCK_NAME, TEST_LOCK_DURATION);
        assertNotNull(lock);
        Optional<Lock> lockReadFromDatabase = lockDao.selectLockIfExists(TEST_LOCK_NAME);
        assertTrue(lockReadFromDatabase.isPresent());
        assertThat(lock, is(lockReadFromDatabase.get()));

        lockDao.releaseLock(lock);
        lockReadFromDatabase = lockDao.selectLockIfExists(TEST_LOCK_NAME);
        assertFalse(lockReadFromDatabase.isPresent());
    }

    @Test
    public void lock_cannot_be_acquired_by_another_owner_when_it_is_taken_and_still_valid() throws Exception {
        testTakenLockAcquisitionWithAnotherOwnerName("another-owner");
    }

    @Test
    public void lock_cannot_be_acquired_even_by_same_owner_when_it_is_taken_and_still_valid() throws Exception {
        testTakenLockAcquisitionWithAnotherOwnerName(LOCK_OWNER_NAME);
    }

    private void testTakenLockAcquisitionWithAnotherOwnerName(String anotherLockOwnerName) throws Exception {
        // Acquire the lock (win the race for the lock)
        Lock winningLock = runTxInOtherThread(tx -> lockDao.acquireLock(TEST_LOCK_NAME, TEST_LOCK_DURATION)).get();
        assertNotNull(winningLock);
        assertThat(winningLock.owner, is(LOCK_OWNER_NAME));

        // Try to acquire it with another thread (as if another server)
        LockDao anotherLockDao = new LockDao(queryFactory, validationService, anotherLockOwnerName);
        Exception losingLockException = null;
        Lock losingLock = null;
        try {
            losingLock = runTxInOtherThread(tx -> anotherLockDao.acquireLock(TEST_LOCK_NAME, TEST_LOCK_DURATION)).get();
        } catch (Exception e) {
            losingLockException = e;
        }

        // Verify that another thread did not get the lock & threw an exception
        assertNull(losingLock);
        assertThat(losingLockException, instanceOf(ExecutionException.class));
        assertThat(losingLockException.getCause(), instanceOf(LockAcquireFailedException.class));
    }

    @Test
    public void expired_lock_can_be_claimed_and_releasing_expired_lock_does_not_delete_valid_lock() throws Exception {
        // Acquire the lock that expires immediately
        Lock expiringLock = runTxInOtherThread(tx -> lockDao.acquireLock(TEST_LOCK_NAME, Duration.ZERO)).get();
        assertNotNull(expiringLock);
        assertThat(expiringLock.owner, is(LOCK_OWNER_NAME));

        // New Lock can be claimed when existing lock has expired
        Lock newLock = runTxInOtherThread(tx -> lockDao.acquireLock(TEST_LOCK_NAME, TEST_LOCK_DURATION)).get();
        assertNotNull(newLock);
        assertThat(newLock.owner, is(LOCK_OWNER_NAME));

        // Trying to release the expired lock does not release the valid newLock
        Boolean wasLockReleased = runTxInOtherThread(tx -> lockDao.releaseLock(expiringLock)).get();
        assertFalse(wasLockReleased);

        // Verify that database still contains the valid lock
        Optional<Lock> lockReadFromDatabase = runTxInOtherThread(tx -> lockDao.selectLockIfExists(TEST_LOCK_NAME)).get();
        assertTrue(lockReadFromDatabase.isPresent());
        assertThat(lockReadFromDatabase.get(), equalTo(newLock));
    }

    @Test
    public void cannot_release_lock_that_is_not_owned() {
        thrown.expect(LockException.class);
        thrown.expectMessage("Lock is not owned");

        Lock someoneElsesLock = new Lock(TEST_LOCK_NAME, "someone-else", DateTime.now().plus(TEST_LOCK_DURATION));
        lockDao.releaseLock(someoneElsesLock);
    }

    @Test
    public void lock_acquisition_race_loss_causes_LockAcquireFailedException() throws Exception {
        // Run a thread to acquire a lock: notice that lock is not taken, but wait before inserting the lock to database
        LosingLockDao losingLockDao = new LosingLockDao(queryFactory, validationService, "another-owner");
        Future<Lock> losingLockFuture = runTxInOtherThread(tx -> losingLockDao.acquireLock(TEST_LOCK_NAME, TEST_LOCK_DURATION));
        losingLockDao.waitUntilReadyToInsert();

        // Acquire the lock with another thread (win the race for the lock)
        Lock winningLock = runTxInOtherThread(tx -> lockDao.acquireLock(TEST_LOCK_NAME, TEST_LOCK_DURATION)).get();
        assertNotNull(winningLock);
        assertThat(winningLock.owner, is(LOCK_OWNER_NAME));

        // Let the first thread to proceed with an attempt to acquire lock
        losingLockDao.proceedWithInsertLock();

        // Collect the result of the first thread
        Exception losingLockException = null;
        Lock losingLock = null;
        try {
            losingLock = losingLockFuture.get();
        } catch (Exception e) {
            losingLockException = e;
        }

        // Verify that LosingLockDao did not get the lock & threw an exception
        assertNull(losingLock);
        assertThat(losingLockException, instanceOf(ExecutionException.class));
        assertThat(losingLockException.getCause(), instanceOf(LockAcquireFailedException.class));
    }

    private <T> Future<T> runTxInOtherThread(TransactionCallback<T> transactionCallback) {
        return Executors.newSingleThreadExecutor()
                .submit(() -> {
                    TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
                    txTemplate.setTimeout(1);
                    return txTemplate.execute(transactionCallback);
                });
    }

    public static class LosingLockDao extends LockDao {
        private final Semaphore externalWaitForReadySemaphore = new Semaphore(0);
        private final Semaphore waitForInsertSemaphore = new Semaphore(0);

        public LosingLockDao(PostgreSQLQueryFactory queryFactory, ValidationService validationService,
                             String lockOwnerName) {
            super(queryFactory, validationService, lockOwnerName);
        }

        public void waitUntilReadyToInsert() {
            externalWaitForReadySemaphore.acquireUninterruptibly();
        }

        public void proceedWithInsertLock() {
            waitForInsertSemaphore.release();
        }

        @Override
        public Lock insertLock(String lockName, Duration lockDuration) {
            externalWaitForReadySemaphore.release();
            waitForInsertSemaphore.acquireUninterruptibly();
            return super.insertLock(lockName, lockDuration);
        }
    }
}
