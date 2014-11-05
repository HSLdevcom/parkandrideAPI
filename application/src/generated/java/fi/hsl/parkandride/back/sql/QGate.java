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
 * QGate is a Querydsl query type for QGate
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QGate extends RelationalPathSpatial<QGate> {

    private static final long serialVersionUID = 1614006550;

    public static final QGate gate = new QGate("GATE");

    public final BooleanPath entry = createBoolean("entry");

    public final BooleanPath exit = createBoolean("exit");

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final NumberPath<Integer> gateIndex = createNumber("gateIndex", Integer.class);

    public final GeometryPath<org.geolatte.geom.Geometry> location = createGeometry("location", org.geolatte.geom.Geometry.class);

    public final BooleanPath pedestrian = createBoolean("pedestrian");

    public final com.mysema.query.sql.PrimaryKey<QGate> constraint21 = createPrimaryKey(facilityId, gateIndex);

    public final com.mysema.query.sql.ForeignKey<QFacility> gateFacilityIdFk = createForeignKey(facilityId, "ID");

    public QGate(String variable) {
        super(QGate.class, forVariable(variable), "PUBLIC", "GATE");
        addMetadata();
    }

    public QGate(String variable, String schema, String table) {
        super(QGate.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QGate(Path<? extends QGate> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "GATE");
        addMetadata();
    }

    public QGate(PathMetadata<?> metadata) {
        super(QGate.class, metadata, "PUBLIC", "GATE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(entry, ColumnMetadata.named("ENTRY").withIndex(3).ofType(Types.BOOLEAN).withSize(1).notNull());
        addMetadata(exit, ColumnMetadata.named("EXIT").withIndex(4).ofType(Types.BOOLEAN).withSize(1).notNull());
        addMetadata(facilityId, ColumnMetadata.named("FACILITY_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(gateIndex, ColumnMetadata.named("GATE_INDEX").withIndex(2).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(location, ColumnMetadata.named("LOCATION").withIndex(6).ofType(Types.OTHER).withSize(2147483647).notNull());
        addMetadata(pedestrian, ColumnMetadata.named("PEDESTRIAN").withIndex(5).ofType(Types.BOOLEAN).withSize(1).notNull());
    }

}

