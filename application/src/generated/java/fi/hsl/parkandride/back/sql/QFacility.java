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

    public final NumberPath<Integer> capacityBicycle = createNumber("capacityBicycle", Integer.class);

    public final NumberPath<Integer> capacityCar = createNumber("capacityCar", Integer.class);

    public final NumberPath<Integer> capacityDisabled = createNumber("capacityDisabled", Integer.class);

    public final NumberPath<Integer> capacityElectricCar = createNumber("capacityElectricCar", Integer.class);

    public final NumberPath<Integer> capacityMotorcycle = createNumber("capacityMotorcycle", Integer.class);

    public final NumberPath<Long> emergencyContactId = createNumber("emergencyContactId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final PolygonPath<org.geolatte.geom.Polygon> location = createPolygon("location", org.geolatte.geom.Polygon.class);

    public final StringPath nameEn = createString("nameEn");

    public final StringPath nameFi = createString("nameFi");

    public final StringPath nameSv = createString("nameSv");

    public final NumberPath<Long> operatorContactId = createNumber("operatorContactId", Long.class);

    public final NumberPath<Long> operatorId = createNumber("operatorId", Long.class);

    public final BooleanPath parkAndRideAuthRequired = createBoolean("parkAndRideAuthRequired");

    public final StringPath paymentInfoDetailEn = createString("paymentInfoDetailEn");

    public final StringPath paymentInfoDetailFi = createString("paymentInfoDetailFi");

    public final StringPath paymentInfoDetailSv = createString("paymentInfoDetailSv");

    public final StringPath paymentInfoUrlEn = createString("paymentInfoUrlEn");

    public final StringPath paymentInfoUrlFi = createString("paymentInfoUrlFi");

    public final StringPath paymentInfoUrlSv = createString("paymentInfoUrlSv");

    public final NumberPath<Long> serviceContactId = createNumber("serviceContactId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QFacility> constraint4c = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QContact> facilityOperatorContactIdFk = createForeignKey(operatorContactId, "ID");

    public final com.mysema.query.sql.ForeignKey<QOperator> facilityOperatorIdFk = createForeignKey(operatorId, "ID");

    public final com.mysema.query.sql.ForeignKey<QContact> facilityServiceContactIdFk = createForeignKey(serviceContactId, "ID");

    public final com.mysema.query.sql.ForeignKey<QContact> facilityEmergencyContactIdFk = createForeignKey(emergencyContactId, "ID");

    public final com.mysema.query.sql.ForeignKey<QFacilityAlias> _facilityAliasFk = createInvForeignKey(id, "FACILITY_ID");

    public final com.mysema.query.sql.ForeignKey<QFacilityStatus> _facilityStatusFacilityIdFk = createInvForeignKey(id, "FACILITY_ID");

    public final com.mysema.query.sql.ForeignKey<QPricing> _pricingFacilityIdFk = createInvForeignKey(id, "FACILITY_ID");

    public final com.mysema.query.sql.ForeignKey<QFacilityService> _facilityServiceFacilityIdFk = createInvForeignKey(id, "FACILITY_ID");

    public final com.mysema.query.sql.ForeignKey<QUnavailableCapacity> _unavailableCapacityFacilityIdFk = createInvForeignKey(id, "FACILITY_ID");

    public final com.mysema.query.sql.ForeignKey<QFacilityPaymentMethod> _facilityPaymentMethodFacilityIdFk = createInvForeignKey(id, "FACILITY_ID");

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
        addMetadata(capacityBicycle, ColumnMetadata.named("CAPACITY_BICYCLE").withIndex(21).ofType(Types.INTEGER).withSize(10));
        addMetadata(capacityCar, ColumnMetadata.named("CAPACITY_CAR").withIndex(17).ofType(Types.INTEGER).withSize(10));
        addMetadata(capacityDisabled, ColumnMetadata.named("CAPACITY_DISABLED").withIndex(18).ofType(Types.INTEGER).withSize(10));
        addMetadata(capacityElectricCar, ColumnMetadata.named("CAPACITY_ELECTRIC_CAR").withIndex(19).ofType(Types.INTEGER).withSize(10));
        addMetadata(capacityMotorcycle, ColumnMetadata.named("CAPACITY_MOTORCYCLE").withIndex(20).ofType(Types.INTEGER).withSize(10));
        addMetadata(emergencyContactId, ColumnMetadata.named("EMERGENCY_CONTACT_ID").withIndex(7).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(location, ColumnMetadata.named("LOCATION").withIndex(5).ofType(Types.OTHER).withSize(2147483647).notNull());
        addMetadata(nameEn, ColumnMetadata.named("NAME_EN").withIndex(4).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(nameFi, ColumnMetadata.named("NAME_FI").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(nameSv, ColumnMetadata.named("NAME_SV").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(operatorContactId, ColumnMetadata.named("OPERATOR_CONTACT_ID").withIndex(8).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(operatorId, ColumnMetadata.named("OPERATOR_ID").withIndex(6).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(parkAndRideAuthRequired, ColumnMetadata.named("PARK_AND_RIDE_AUTH_REQUIRED").withIndex(10).ofType(Types.BOOLEAN).withSize(1).notNull());
        addMetadata(paymentInfoDetailEn, ColumnMetadata.named("PAYMENT_INFO_DETAIL_EN").withIndex(15).ofType(Types.VARCHAR).withSize(255));
        addMetadata(paymentInfoDetailFi, ColumnMetadata.named("PAYMENT_INFO_DETAIL_FI").withIndex(11).ofType(Types.VARCHAR).withSize(255));
        addMetadata(paymentInfoDetailSv, ColumnMetadata.named("PAYMENT_INFO_DETAIL_SV").withIndex(13).ofType(Types.VARCHAR).withSize(255));
        addMetadata(paymentInfoUrlEn, ColumnMetadata.named("PAYMENT_INFO_URL_EN").withIndex(16).ofType(Types.VARCHAR).withSize(255));
        addMetadata(paymentInfoUrlFi, ColumnMetadata.named("PAYMENT_INFO_URL_FI").withIndex(12).ofType(Types.VARCHAR).withSize(255));
        addMetadata(paymentInfoUrlSv, ColumnMetadata.named("PAYMENT_INFO_URL_SV").withIndex(14).ofType(Types.VARCHAR).withSize(255));
        addMetadata(serviceContactId, ColumnMetadata.named("SERVICE_CONTACT_ID").withIndex(9).ofType(Types.BIGINT).withSize(19));
    }

}

