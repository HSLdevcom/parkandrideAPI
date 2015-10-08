package fi.hsl.parkandride.back.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;

import com.mysema.query.sql.spatial.RelationalPathSpatial;

import com.mysema.query.spatial.path.*;



/**
 * QRequestLog is a Querydsl query type for QRequestLog
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QRequestLog extends RelationalPathSpatial<QRequestLog> {

    private static final long serialVersionUID = 404738624;

    public static final QRequestLog requestLog = new QRequestLog("REQUEST_LOG");

    public final NumberPath<Long> count = createNumber("count", Long.class);

    public final NumberPath<Long> sourceId = createNumber("sourceId", Long.class);

    public final DateTimePath<org.joda.time.DateTime> ts = createDateTime("ts", org.joda.time.DateTime.class);

    public final NumberPath<Long> urlId = createNumber("urlId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QRequestLog> constraintC = createPrimaryKey(sourceId, ts, urlId);

    public final com.mysema.query.sql.ForeignKey<QRequestLogUrl> requestLogUrlId = createForeignKey(urlId, "ID");

    public final com.mysema.query.sql.ForeignKey<QRequestLogSource> requestLogSourceId = createForeignKey(sourceId, "ID");

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

    public QRequestLog(PathMetadata<?> metadata) {
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

