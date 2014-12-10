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
 * QFacility is a Querydsl query type for QFacility
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QFacility extends RelationalPathSpatial<QFacility> {

    private static final long serialVersionUID = -1679504018;

    public static final QFacility facility = new QFacility("FACILITY");

    public final NumberPath<Long> emergencyContactId = createNumber("emergencyContactId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final GeometryPath<org.geolatte.geom.Geometry> location = createGeometry("location", org.geolatte.geom.Geometry.class);

    public final StringPath nameEn = createString("nameEn");

    public final StringPath nameFi = createString("nameFi");

    public final StringPath nameSv = createString("nameSv");

    public final NumberPath<Long> operatorContactId = createNumber("operatorContactId", Long.class);

    public final NumberPath<Long> operatorId = createNumber("operatorId", Long.class);

    public final NumberPath<Long> serviceContactId = createNumber("serviceContactId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QFacility> constraint4 = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QContact> facilityOperatorContactIdFk = createForeignKey(operatorContactId, "ID");

    public final com.mysema.query.sql.ForeignKey<QOperator> facilityOperatorIdFk = createForeignKey(operatorId, "ID");

    public final com.mysema.query.sql.ForeignKey<QContact> facilityServiceContactIdFk = createForeignKey(serviceContactId, "ID");

    public final com.mysema.query.sql.ForeignKey<QContact> facilityEmergencyContactIdFk = createForeignKey(emergencyContactId, "ID");

    public final com.mysema.query.sql.ForeignKey<QFacilityAlias> _facilityAliasFk = createInvForeignKey(id, "FACILITY_ID");

    public final com.mysema.query.sql.ForeignKey<QCapacity> _capacityFacilityIdFk = createInvForeignKey(id, "FACILITY_ID");

    public final com.mysema.query.sql.ForeignKey<QFacilityService> _facilityServiceFacilityIdFk = createInvForeignKey(id, "FACILITY_ID");

    public final com.mysema.query.sql.ForeignKey<QPort> _portFacilityIdFk = createInvForeignKey(id, "FACILITY_ID");

    public QFacility(String variable) {
        super(QFacility.class, forVariable(variable), "PUBLIC", "FACILITY");
        addMetadata();
    }

    public QFacility(String variable, String schema, String table) {
        super(QFacility.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFacility(Path<? extends QFacility> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "FACILITY");
        addMetadata();
    }

    public QFacility(PathMetadata<?> metadata) {
        super(QFacility.class, metadata, "PUBLIC", "FACILITY");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(emergencyContactId, ColumnMetadata.named("EMERGENCY_CONTACT_ID").withIndex(7).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(location, ColumnMetadata.named("LOCATION").withIndex(5).ofType(Types.OTHER).withSize(2147483647).notNull());
        addMetadata(nameEn, ColumnMetadata.named("NAME_EN").withIndex(4).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(nameFi, ColumnMetadata.named("NAME_FI").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(nameSv, ColumnMetadata.named("NAME_SV").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(operatorContactId, ColumnMetadata.named("OPERATOR_CONTACT_ID").withIndex(8).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(operatorId, ColumnMetadata.named("OPERATOR_ID").withIndex(6).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(serviceContactId, ColumnMetadata.named("SERVICE_CONTACT_ID").withIndex(9).ofType(Types.BIGINT).withSize(19));
    }

}

