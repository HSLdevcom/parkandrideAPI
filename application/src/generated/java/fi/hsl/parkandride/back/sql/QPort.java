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
 * QPort is a Querydsl query type for QPort
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QPort extends RelationalPathSpatial<QPort> {

    private static final long serialVersionUID = 1614288076;

    public static final QPort port = new QPort("PORT");

    public final BooleanPath entry = createBoolean("entry");

    public final BooleanPath exit = createBoolean("exit");

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final GeometryPath<org.geolatte.geom.Geometry> location = createGeometry("location", org.geolatte.geom.Geometry.class);

    public final BooleanPath pedestrian = createBoolean("pedestrian");

    public final NumberPath<Integer> portIndex = createNumber("portIndex", Integer.class);

    public final com.mysema.query.sql.PrimaryKey<QPort> constraint25 = createPrimaryKey(facilityId, portIndex);

    public final com.mysema.query.sql.ForeignKey<QFacility> portFacilityIdFk = createForeignKey(facilityId, "ID");

    public QPort(String variable) {
        super(QPort.class, forVariable(variable), "PUBLIC", "PORT");
        addMetadata();
    }

    public QPort(String variable, String schema, String table) {
        super(QPort.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPort(Path<? extends QPort> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "PORT");
        addMetadata();
    }

    public QPort(PathMetadata<?> metadata) {
        super(QPort.class, metadata, "PUBLIC", "PORT");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(entry, ColumnMetadata.named("ENTRY").withIndex(3).ofType(Types.BOOLEAN).withSize(1).notNull());
        addMetadata(exit, ColumnMetadata.named("EXIT").withIndex(4).ofType(Types.BOOLEAN).withSize(1).notNull());
        addMetadata(facilityId, ColumnMetadata.named("FACILITY_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(location, ColumnMetadata.named("LOCATION").withIndex(6).ofType(Types.OTHER).withSize(2147483647).notNull());
        addMetadata(pedestrian, ColumnMetadata.named("PEDESTRIAN").withIndex(5).ofType(Types.BOOLEAN).withSize(1).notNull());
        addMetadata(portIndex, ColumnMetadata.named("PORT_INDEX").withIndex(2).ofType(Types.INTEGER).withSize(10).notNull());
    }

}

