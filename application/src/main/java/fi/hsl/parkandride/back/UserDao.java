package fi.hsl.parkandride.back;

import org.joda.time.DateTime;

import com.mysema.query.Tuple;
import com.mysema.query.sql.SQLExpressions;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.postgres.PostgresQuery;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.expr.DateTimeExpression;
import com.mysema.query.types.expr.SimpleExpression;

import fi.hsl.parkandride.back.sql.QAppUser;
import fi.hsl.parkandride.core.back.UserRepository;
import fi.hsl.parkandride.core.domain.NotFoundException;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.domain.UserSearch;
import fi.hsl.parkandride.core.domain.UserSecret;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;

public class UserDao implements UserRepository {

    public static final String USER_ID_SEQ = "user_id_seq";

    private static final SimpleExpression<Long> nextUserId = SQLExpressions.nextval(USER_ID_SEQ);

    private static final DateTimeExpression<DateTime> currentTime = DateTimeExpression.currentTimestamp(DateTime.class);

    private static final QAppUser qUser = QAppUser.appUser;

    private static final MappingProjection<User> userMapping = new MappingProjection<User>(User.class,
            qUser.id, qUser.username, qUser.role, qUser.operatorId) {

        @Override
        protected User map(Tuple row) {
            User user = new User();
            user.id = row.get(qUser.id);
            user.username = row.get(qUser.username);
            user.role = row.get(qUser.role);
            user.operatorId = row.get(qUser.operatorId);
            return user;
        }
    };


    private static final MappingProjection<UserSecret> userSecretMapping = new MappingProjection<UserSecret>(UserSecret.class,
            qUser.password, qUser.minTokenTimestamp, userMapping) {
        @Override
        protected UserSecret map(Tuple row) {
            UserSecret userSecret = new UserSecret();
            userSecret.password = row.get(qUser.password);
            userSecret.minTokenTimestamp = row.get(qUser.minTokenTimestamp);
            System.out.println("timestamp from db is (DateTime)" + userSecret.minTokenTimestamp);
            System.out.println("timestamp from db is (millis)" + userSecret.minTokenTimestamp.getMillis());
//            userSecret.secret = row.get(qUser.secret);
            userSecret.user = row.get(userMapping);
            return userSecret;
        }
    };


    private final PostgresQueryFactory queryFactory;

    public UserDao(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }


    @Override
    @TransactionalWrite
    public long insertUser(UserSecret userSecret) {
        return insertUser(userSecret, queryFactory.query().singleResult(nextUserId));
    }

    @TransactionalWrite
    public long insertUser(UserSecret userSecret, long userId) {
        SQLInsertClause insert = queryFactory.insert(qUser);
        insert.set(qUser.id, userId)
                .set(qUser.password, userSecret.password)
                .set(qUser.minTokenTimestamp, currentTime)

                .set(qUser.username, userSecret.user.username.toLowerCase())
                .set(qUser.role, userSecret.user.role)
                .set(qUser.operatorId, userSecret.user.operatorId);
        insert.execute();
        return userId;
    }

    @TransactionalRead
    @Override
    public DateTime getCurrentTime() {
        return queryFactory.query().singleResult(currentTime);
    }

    @TransactionalWrite
    @Override
    public void revokeTokens(long userId, DateTime asOf) {
        System.out.println("revoking token for user " + userId);
        if (queryFactory.update(qUser)
                .where(qUser.id.eq(userId))
                .set(qUser.minTokenTimestamp, asOf)
                .execute() != 1) {
            notFound(userId);
        }
    }

    @TransactionalWrite
    @Override
    public void updatePassword(long userId, String password) {
        if (queryFactory.update(qUser)
                .where(qUser.id.eq(userId))
                .set(qUser.password, password)
                .set(qUser.minTokenTimestamp, currentTime)
                .execute() != 1) {
            notFound(userId);
        } else {
            System.out.println("updated passwd for user " + userId + " new token timestamp is " + currentTime);
        }
    }

    @TransactionalWrite
    @Override
    public void updateUser(long userId, User user) {
        if (queryFactory.update(qUser)
                .where(qUser.id.eq(userId))
                .set(qUser.username, user.username.toLowerCase())
                .set(qUser.role, user.role)
                .execute() != 1) {
            notFound(userId);
        }
    }

    @TransactionalRead
    @Override
    public UserSecret getUser(String username) {
        UserSecret userSecret = queryFactory
                .from(qUser)
                .where(qUser.username.eq(username.toLowerCase()))
                .singleResult(userSecretMapping);
        if (userSecret == null) {
            notFound(username);
        }
        return userSecret;
    }

    private void notFound(String username) {
        throw new NotFoundException("User by username '%s'", username);
    }

    private void notFound(long userId) {
        throw new NotFoundException("User by id '%s'", userId);
    }

    @TransactionalRead
    @Override
    public UserSecret getUser(long userId) {
        UserSecret userSecret = queryFactory.from(qUser).where(qUser.id.eq(userId)).singleResult(userSecretMapping);
        if (userSecret == null) {
            notFound(userId);
        }
        return userSecret;
    }

    @Override
    @TransactionalRead
    public SearchResults<User> findUsers(UserSearch search) {
        PostgresQuery qry = queryFactory.from(qUser);
        qry.limit(search.limit + 1);
        qry.offset(search.offset);

        if (search.operatorId != null) {
            qry.where(qUser.operatorId.eq(search.operatorId));
        }

        qry.orderBy(qUser.username.asc());

        return SearchResults.of(qry.list(userMapping), search.limit);
    }

}
