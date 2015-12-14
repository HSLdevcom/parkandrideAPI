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
import fi.hsl.parkandride.core.service.ValidationService;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class LockDao implements LockRepository {

    private final Logger log = LoggerFactory.getLogger(LockDao.class);
    private static final QLock qLock = QLock.lock;

    private static final MappingProjection<Lock> lockMapping = new MappingProjection<Lock>(Lock.class, qLock.all()) {
        @Override
        protected Lock map(Tuple row) {
            return new Lock(row.get(qLock.name), row.get(qLock.owner), row.get(qLock.validUntil));
        }
    };

    private final PostgreSQLQueryFactory queryFactory;
    private final ValidationService validationService;

    public LockDao(PostgreSQLQueryFactory queryFactory, ValidationService validationService) {
        this.queryFactory = queryFactory;
        this.validationService = validationService;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public Optional<Lock> acquireLock(String lockName, String owner, Duration lockDuration) {
        Optional<Lock> lock = selectLockIfExists(lockName);
        if (lock.isPresent()) {
            Lock existingLock = lock.get();
            if (existingLock.validUntil.isBefore(DateTime.now())) {
                return claimExpiredLock(existingLock, owner, lockDuration);
            } else {
                return Optional.empty(); // existing lock is still valid
            }
        }
        return insertLock(lockName, owner, lockDuration);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public void releaseLock(Lock lock) {
        validationService.validate(lock);
        deleteLock(lock);
    }

    private Optional<Lock> selectLockIfExists(String lockName) {
        return Optional.of(queryFactory.from(qLock)
                .where(qLock.name.eq(lockName))
                .select(lockMapping)
                .fetchOne());
    }

    private Optional<Lock> claimExpiredLock(Lock existingLock, String newOwner, Duration lockDuration) {
        try {
            final DateTime newValidUntil = DateTime.now().plus(lockDuration);
            long rowsUpdated = queryFactory.update(qLock)
                    .where(qLock.name.eq(existingLock.name))
                    .where(qLock.owner.eq(existingLock.owner))
                    .where(qLock.validUntil.eq(existingLock.validUntil))
                    .set(qLock.owner, newOwner)
                    .set(qLock.validUntil, newValidUntil)
                    .execute();
            if (rowsUpdated > 0) {
                return Optional.of(new Lock(existingLock.name, newOwner, newValidUntil));
            }
        } catch (QueryException e) {
            log.debug("Claiming lock {} for {} failed", existingLock, newOwner, e);
            // Fall through to return Optional.empty() if update fails e.g. due to race condition
        }
        return Optional.empty();
    }

    private Optional<Lock> insertLock(String lockName, String owner, Duration lockDuration) {
        final DateTime validUntil = DateTime.now().plus(lockDuration);
        final Lock newLock = new Lock(lockName, owner, validUntil);
        try {
            long rowsInserted = queryFactory.insert(qLock)
                    .columns(qLock.name, qLock.owner, qLock.validUntil)
                    .values(lockName, owner, validUntil)
                    .execute();
            if (rowsInserted > 0) {
                return Optional.of(newLock);
            }
        } catch (QueryException e) {
            log.debug("Failed to get lock {} (insert to DB failed)", newLock, e);
        }
        return Optional.empty();
    }

    private void deleteLock(Lock lock) {
        queryFactory.delete(qLock)
                .where(qLock.name.eq(lock.name))
                .where(qLock.owner.eq(lock.owner))
                .execute();
    }
}
