package fi.hsl.parkandride.back.sql;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.spatial.RelationalPathSpatial;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;



/**
 * QRequestLogSource is a Querydsl query type for QRequestLogSource
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QRequestLogSource extends RelationalPathSpatial<QRequestLogSource> {

    private static final long serialVersionUID = -285313413;

    public static final QRequestLogSource requestLogSource = new QRequestLogSource("REQUEST_LOG_SOURCE");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath source = createString("source");

    public final com.querydsl.sql.PrimaryKey<QRequestLogSource> constraint2fe = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<QRequestLog> _requestLogSourceId = createInvForeignKey(id, "SOURCE_ID");

    public QRequestLogSource(String variable) {
        super(QRequestLogSource.class, forVariable(variable), "PUBLIC", "REQUEST_LOG_SOURCE");
        addMetadata();
    }

    public QRequestLogSource(String variable, String schema, String table) {
        super(QRequestLogSource.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QRequestLogSource(Path<? extends QRequestLogSource> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "REQUEST_LOG_SOURCE");
        addMetadata();
    }

    public QRequestLogSource(PathMetadata metadata) {
        super(QRequestLogSource.class, metadata, "PUBLIC", "REQUEST_LOG_SOURCE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(source, ColumnMetadata.named("SOURCE").withIndex(2).ofType(Types.VARCHAR).withSize(128));
    }

}

