package fi.hsl.parkandride.back.sql;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.spatial.RelationalPathSpatial;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;



/**
 * QRequestLog is a Querydsl query type for QRequestLog
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QRequestLog extends RelationalPathSpatial<QRequestLog> {

    private static final long serialVersionUID = 404738624;

    public static final QRequestLog requestLog = new QRequestLog("REQUEST_LOG");

    public final NumberPath<Long> count = createNumber("count", Long.class);

    public final NumberPath<Long> sourceId = createNumber("sourceId", Long.class);

    public final DateTimePath<org.joda.time.DateTime> ts = createDateTime("ts", org.joda.time.DateTime.class);

    public final NumberPath<Long> urlId = createNumber("urlId", Long.class);

    public final com.querydsl.sql.PrimaryKey<QRequestLog> constraintC = createPrimaryKey(sourceId, ts, urlId);

    public final com.querydsl.sql.ForeignKey<QRequestLogUrl> requestLogUrlId = createForeignKey(urlId, "ID");

    public final com.querydsl.sql.ForeignKey<QRequestLogSource> requestLogSourceId = createForeignKey(sourceId, "ID");

    public QRequestLog(String variable) {
        super(QRequestLog.class, forVariable(variable), "PUBLIC", "REQUEST_LOG");
        addMetadata();
    }

    public QRequestLog(String variable, String schema, String table) {
        super(QRequestLog.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QRequestLog(Path<? extends QRequestLog> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "REQUEST_LOG");
        addMetadata();
    }

    public QRequestLog(PathMetadata metadata) {
        super(QRequestLog.class, metadata, "PUBLIC", "REQUEST_LOG");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(count, ColumnMetadata.named("COUNT").withIndex(4).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(sourceId, ColumnMetadata.named("SOURCE_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(ts, ColumnMetadata.named("TS").withIndex(3).ofType(Types.TIMESTAMP).withSize(23).withDigits(10).notNull());
        addMetadata(urlId, ColumnMetadata.named("URL_ID").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

