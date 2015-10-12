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
 * QRequestLogUrl is a Querydsl query type for QRequestLogUrl
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QRequestLogUrl extends RelationalPathSpatial<QRequestLogUrl> {

    private static final long serialVersionUID = 1595233039;

    public static final QRequestLogUrl requestLogUrl = new QRequestLogUrl("REQUEST_LOG_URL");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath url = createString("url");

    public final com.querydsl.sql.PrimaryKey<QRequestLogUrl> constraint763 = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<QRequestLog> _requestLogUrlId = createInvForeignKey(id, "URL_ID");

    public QRequestLogUrl(String variable) {
        super(QRequestLogUrl.class, forVariable(variable), "PUBLIC", "REQUEST_LOG_URL");
        addMetadata();
    }

    public QRequestLogUrl(String variable, String schema, String table) {
        super(QRequestLogUrl.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QRequestLogUrl(Path<? extends QRequestLogUrl> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "REQUEST_LOG_URL");
        addMetadata();
    }

    public QRequestLogUrl(PathMetadata metadata) {
        super(QRequestLogUrl.class, metadata, "PUBLIC", "REQUEST_LOG_URL");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(url, ColumnMetadata.named("URL").withIndex(2).ofType(Types.VARCHAR).withSize(128));
    }

}

