package fi.hsl.parkandride.outbound;

import com.mysema.query.sql.RelationalPath;
import com.mysema.query.sql.postgres.PostgresQueryFactory;

import fi.hsl.parkandride.core.service.TransactionalWrite;

public class TestHelper {

    private final PostgresQueryFactory queryFactory;

    public TestHelper(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @TransactionalWrite
    public void clear(RelationalPath... tables) {
        for (RelationalPath table : tables) {
            queryFactory.delete(table).execute();
        }
    }
}
