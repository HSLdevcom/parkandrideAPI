package fi.hsl.parkandride.outbound.sql;

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
 * QHub is a Querydsl query type for QHub
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QHub extends RelationalPathSpatial<QHub> {

    private static final long serialVersionUID = -1352658975;

    public static final QHub hub = new QHub("HUB");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final GeometryPath<org.geolatte.geom.Geometry> location = createGeometry("location", org.geolatte.geom.Geometry.class);

    public final StringPath name = createString("name");

    public final com.mysema.query.sql.PrimaryKey<QHub> constraint1 = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QHubFacility> _hubFacilityHubIdFk = createInvForeignKey(id, "HUB_ID");

    public QHub(String variable) {
        super(QHub.class, forVariable(variable), "PUBLIC", "HUB");
        addMetadata();
    }

    public QHub(String variable, String schema, String table) {
        super(QHub.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QHub(Path<? extends QHub> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "HUB");
        addMetadata();
    }

    public QHub(PathMetadata<?> metadata) {
        super(QHub.class, metadata, "PUBLIC", "HUB");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(location, ColumnMetadata.named("LOCATION").withIndex(3).ofType(Types.OTHER).withSize(2147483647).notNull());
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
    }

}

