// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import com.querydsl.sql.postgresql.PostgreSQLQueryFactory;
import fi.hsl.parkandride.core.domain.Lock;
import fi.hsl.parkandride.core.domain.LockException;
import fi.hsl.parkandride.core.service.ValidationService;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Transactional
public class LockDaoTest extends AbstractDaoTest {

    private static final String LOCK_OWNER_NAME = "test-lock-owner";
    private static final String TEST_LOCK_NAME = "test-lock";
    private static final Duration TEST_LOCK_DURATION = Duration.standardSeconds(10);

    @Inject
    private PostgreSQLQueryFactory queryFactory;

    @Inject
    private ValidationService validationService;

    private LockDao lockDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void createLockDao() {
        lockDao = new LockDao(queryFactory, validationService, LOCK_OWNER_NAME);
    }

    @Test
    public void lock_acquisition_creates_a_lock_in_database() {
        Lock lock = lockDao.acquireLock(TEST_LOCK_NAME, TEST_LOCK_DURATION);
        assertNotNull(lock);
        Optional<Lock> lockReadFromDatabase = lockDao.selectLockIfExists(TEST_LOCK_NAME);
        assertTrue(lockReadFromDatabase.isPresent());
        assertThat(lock, is(lockReadFromDatabase.get()));
    }

    @Test
    public void cannot_release_lock_that_is_not_owned() {
        thrown.expect(LockException.class);
        thrown.expectMessage("Lock is not owned");

        Lock someoneElsesLock = new Lock(TEST_LOCK_NAME, "someone-else", DateTime.now().plus(TEST_LOCK_DURATION));
        lockDao.releaseLock(someoneElsesLock);
    }
}
