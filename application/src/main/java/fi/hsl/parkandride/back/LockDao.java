// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import com.querydsl.core.QueryException;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.MappingProjection;
import com.querydsl.sql.postgresql.PostgreSQLQueryFactory;
import fi.hsl.parkandride.back.sql.QLock;
import fi.hsl.parkandride.core.back.LockRepository;
import fi.hsl.parkandride.core.domain.Lock;
import fi.hsl.parkandride.core.domain.LockAcquireFailedException;
import fi.hsl.parkandride.core.domain.LockException;
import fi.hsl.parkandride.core.service.ValidationService;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class LockDao implements LockRepository {

    private static final QLock qLock = QLock.lock;

    private static final MappingProjection<Lock> lockMapping = new MappingProjection<Lock>(Lock.class, qLock.all()) {
        @Override
        protected Lock map(Tuple row) {
            return new Lock(row.get(qLock.name), row.get(qLock.owner), row.get(qLock.validUntil));
        }
    };

    private final PostgreSQLQueryFactory queryFactory;
    private final ValidationService validationService;
    private final String ownerName;

    public LockDao(PostgreSQLQueryFactory queryFactory, ValidationService validationService, String lockOwnerName) {
        this.queryFactory = queryFactory;
        this.validationService = validationService;
        this.ownerName = lockOwnerName;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public Lock acquireLock(String lockName, Duration lockDuration) {
        Optional<Lock> lock = selectLockIfExists(lockName);
        if (lock.isPresent()) {
            Lock existingLock = lock.get();
            if (existingLock.validUntil.isBefore(DateTime.now())) {
                return claimExpiredLock(existingLock, lockDuration);
            } else {
                throw new LockAcquireFailedException("Existing lock " + existingLock + " is still valid");
            }
        }
        return insertLock(lockName, lockDuration);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public boolean releaseLock(Lock lock) {
        validationService.validate(lock);
        if (ownerName.equals(lock.owner)) {
            return deleteLock(lock) == 1;
        } else {
            throw new LockException("Cannot release lock. Lock is not owned by this node.");
        }
    }

    // Following mehods are protected to allow testing

    protected Optional<Lock> selectLockIfExists(String lockName) {
        return Optional.ofNullable(queryFactory.from(qLock)
                .where(qLock.name.eq(lockName))
                .select(lockMapping)
                .fetchOne());
    }

    protected Lock claimExpiredLock(Lock existingLock, Duration lockDuration) {
        try {
            final DateTime newValidUntil = DateTime.now().plus(lockDuration);
            long rowsUpdated = queryFactory.update(qLock)
                    .where(qLock.name.eq(existingLock.name))
                    .where(qLock.owner.eq(existingLock.owner))
                    .where(qLock.validUntil.eq(existingLock.validUntil))
                    .set(qLock.owner, ownerName)
                    .set(qLock.validUntil, newValidUntil)
                    .execute();
            if (rowsUpdated > 0) {
                return new Lock(existingLock.name, ownerName, newValidUntil);
            } else {
                throw getFailedToClaimExpiredLockException(existingLock, null);
            }
        } catch (QueryException e) {
            throw getFailedToClaimExpiredLockException(existingLock, e);
        }
    }

    private LockAcquireFailedException getFailedToClaimExpiredLockException(Lock existingLock, QueryException e) {
        return new LockAcquireFailedException("Failed to claim expired lock " + existingLock + " for " + ownerName, e);
    }

    protected Lock insertLock(String lockName, Duration lockDuration) {
        final DateTime validUntil = DateTime.now().plus(lockDuration);
        final Lock newLock = new Lock(lockName, ownerName, validUntil);
        try {
            long rowsInserted = queryFactory.insert(qLock)
                    .columns(qLock.name, qLock.owner, qLock.validUntil)
                    .values(lockName, ownerName, validUntil)
                    .execute();
            if (rowsInserted > 0) {
                return newLock;
            } else {
                throw getInsertLockFailedException(lockName, null);
            }
        } catch (QueryException e) {
            throw getInsertLockFailedException(lockName, e);
        }
    }

    private LockAcquireFailedException getInsertLockFailedException(String lockName, QueryException e) {
        return new LockAcquireFailedException("Failed to acquire lock '" + lockName + "' (lost acquisition race to another node)", e);
    }

    protected long deleteLock(Lock lock) {
        return queryFactory.delete(qLock)
                .where(qLock.name.eq(lock.name))
                .where(qLock.owner.eq(lock.owner))
                .where(qLock.validUntil.eq(lock.validUntil))
                .execute();
    }
}
