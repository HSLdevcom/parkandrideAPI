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

    public final NumberPath<Integer> capacityBicycleSecureSpace = createNumber("capacityBicycleSecureSpace", Integer.class);

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

    public final StringPath openingHoursInfoEn = createString("openingHoursInfoEn");

    public final StringPath openingHoursInfoFi = createString("openingHoursInfoFi");

    public final StringPath openingHoursInfoSv = createString("openingHoursInfoSv");

    public final StringPath openingHoursUrlEn = createString("openingHoursUrlEn");

    public final StringPath openingHoursUrlFi = createString("openingHoursUrlFi");

    public final StringPath openingHoursUrlSv = createString("openingHoursUrlSv");

    public final NumberPath<Long> operatorContactId = createNumber("operatorContactId", Long.class);

    public final NumberPath<Long> operatorId = createNumber("operatorId", Long.class);

    public final StringPath paymentInfoDetailEn = createString("paymentInfoDetailEn");

    public final StringPath paymentInfoDetailFi = createString("paymentInfoDetailFi");

    public final StringPath paymentInfoDetailSv = createString("paymentInfoDetailSv");

    public final StringPath paymentInfoUrlEn = createString("paymentInfoUrlEn");

    public final StringPath paymentInfoUrlFi = createString("paymentInfoUrlFi");

    public final StringPath paymentInfoUrlSv = createString("paymentInfoUrlSv");

    public final EnumPath<fi.hsl.parkandride.core.domain.PricingMethod> pricingMethod = createEnum("pricingMethod", fi.hsl.parkandride.core.domain.PricingMethod.class);

    public final NumberPath<Long> serviceContactId = createNumber("serviceContactId", Long.class);

    public final EnumPath<fi.hsl.parkandride.core.domain.FacilityStatus> status = createEnum("status", fi.hsl.parkandride.core.domain.FacilityStatus.class);

    public final StringPath statusDescriptionEn = createString("statusDescriptionEn");

    public final StringPath statusDescriptionFi = createString("statusDescriptionFi");

    public final StringPath statusDescriptionSv = createString("statusDescriptionSv");

    public final BooleanPath usageCommercial = createBoolean("usageCommercial");

    public final BooleanPath usageHsl = createBoolean("usageHsl");

    public final BooleanPath usageParkAndRide = createBoolean("usageParkAndRide");

    public final com.mysema.query.sql.PrimaryKey<QFacility> constraint4c = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QContact> facilityOperatorContactIdFk = createForeignKey(operatorContactId, "ID");

    public final com.mysema.query.sql.ForeignKey<QOperator> facilityOperatorIdFk = createForeignKey(operatorId, "ID");

    public final com.mysema.query.sql.ForeignKey<QPricingMethod> facilityPricingMethodFk = createForeignKey(pricingMethod, "NAME");

    public final com.mysema.query.sql.ForeignKey<QContact> facilityServiceContactIdFk = createForeignKey(serviceContactId, "ID");

    public final com.mysema.query.sql.ForeignKey<QContact> facilityEmergencyContactIdFk = createForeignKey(emergencyContactId, "ID");

    public final com.mysema.query.sql.ForeignKey<QFacilityStatus> facilityStatusFk = createForeignKey(status, "NAME");

    public final com.mysema.query.sql.ForeignKey<QFacilityAlias> _facilityAliasFk = createInvForeignKey(id, "FACILITY_ID");

    public final com.mysema.query.sql.ForeignKey<QFacilityUtilization> _facilityUtilizationFacilityIdFk = createInvForeignKey(id, "FACILITY_ID");

    public final com.mysema.query.sql.ForeignKey<QPricing> _pricingFacilityIdFk = createInvForeignKey(id, "FACILITY_ID");

    public final com.mysema.query.sql.ForeignKey<QFacilityService> _facilityServiceFacilityIdFk = createInvForeignKey(id, "FACILITY_ID");

    public final com.mysema.query.sql.ForeignKey<QPredictor> _predictorFacilityIdFk = createInvForeignKey(id, "FACILITY_ID");

    public final com.mysema.query.sql.ForeignKey<QUnavailableCapacity> _unavailableCapacityFacilityIdFk = createInvForeignKey(id, "FACILITY_ID");

    public final com.mysema.query.sql.ForeignKey<QFacilityPrediction> _facilityPredictionFacilityIdFk = createInvForeignKey(id, "FACILITY_ID");

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
        addMetadata(capacityBicycle, ColumnMetadata.named("CAPACITY_BICYCLE").withIndex(30).ofType(Types.INTEGER).withSize(10));
        addMetadata(capacityBicycleSecureSpace, ColumnMetadata.named("CAPACITY_BICYCLE_SECURE_SPACE").withIndex(31).ofType(Types.INTEGER).withSize(10));
        addMetadata(capacityCar, ColumnMetadata.named("CAPACITY_CAR").withIndex(26).ofType(Types.INTEGER).withSize(10));
        addMetadata(capacityDisabled, ColumnMetadata.named("CAPACITY_DISABLED").withIndex(27).ofType(Types.INTEGER).withSize(10));
        addMetadata(capacityElectricCar, ColumnMetadata.named("CAPACITY_ELECTRIC_CAR").withIndex(28).ofType(Types.INTEGER).withSize(10));
        addMetadata(capacityMotorcycle, ColumnMetadata.named("CAPACITY_MOTORCYCLE").withIndex(29).ofType(Types.INTEGER).withSize(10));
        addMetadata(emergencyContactId, ColumnMetadata.named("EMERGENCY_CONTACT_ID").withIndex(11).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(location, ColumnMetadata.named("LOCATION").withIndex(35).ofType(Types.OTHER).withSize(2147483647).notNull());
        addMetadata(nameEn, ColumnMetadata.named("NAME_EN").withIndex(4).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(nameFi, ColumnMetadata.named("NAME_FI").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(nameSv, ColumnMetadata.named("NAME_SV").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(openingHoursInfoEn, ColumnMetadata.named("OPENING_HOURS_INFO_EN").withIndex(22).ofType(Types.VARCHAR).withSize(255));
        addMetadata(openingHoursInfoFi, ColumnMetadata.named("OPENING_HOURS_INFO_FI").withIndex(20).ofType(Types.VARCHAR).withSize(255));
        addMetadata(openingHoursInfoSv, ColumnMetadata.named("OPENING_HOURS_INFO_SV").withIndex(21).ofType(Types.VARCHAR).withSize(255));
        addMetadata(openingHoursUrlEn, ColumnMetadata.named("OPENING_HOURS_URL_EN").withIndex(25).ofType(Types.VARCHAR).withSize(255));
        addMetadata(openingHoursUrlFi, ColumnMetadata.named("OPENING_HOURS_URL_FI").withIndex(23).ofType(Types.VARCHAR).withSize(255));
        addMetadata(openingHoursUrlSv, ColumnMetadata.named("OPENING_HOURS_URL_SV").withIndex(24).ofType(Types.VARCHAR).withSize(255));
        addMetadata(operatorContactId, ColumnMetadata.named("OPERATOR_CONTACT_ID").withIndex(12).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(operatorId, ColumnMetadata.named("OPERATOR_ID").withIndex(5).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(paymentInfoDetailEn, ColumnMetadata.named("PAYMENT_INFO_DETAIL_EN").withIndex(16).ofType(Types.VARCHAR).withSize(255));
        addMetadata(paymentInfoDetailFi, ColumnMetadata.named("PAYMENT_INFO_DETAIL_FI").withIndex(14).ofType(Types.VARCHAR).withSize(255));
        addMetadata(paymentInfoDetailSv, ColumnMetadata.named("PAYMENT_INFO_DETAIL_SV").withIndex(15).ofType(Types.VARCHAR).withSize(255));
        addMetadata(paymentInfoUrlEn, ColumnMetadata.named("PAYMENT_INFO_URL_EN").withIndex(19).ofType(Types.VARCHAR).withSize(255));
        addMetadata(paymentInfoUrlFi, ColumnMetadata.named("PAYMENT_INFO_URL_FI").withIndex(17).ofType(Types.VARCHAR).withSize(255));
        addMetadata(paymentInfoUrlSv, ColumnMetadata.named("PAYMENT_INFO_URL_SV").withIndex(18).ofType(Types.VARCHAR).withSize(255));
        addMetadata(pricingMethod, ColumnMetadata.named("PRICING_METHOD").withIndex(7).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(serviceContactId, ColumnMetadata.named("SERVICE_CONTACT_ID").withIndex(13).ofType(Types.BIGINT).withSize(19));
        addMetadata(status, ColumnMetadata.named("STATUS").withIndex(6).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(statusDescriptionEn, ColumnMetadata.named("STATUS_DESCRIPTION_EN").withIndex(10).ofType(Types.VARCHAR).withSize(255));
        addMetadata(statusDescriptionFi, ColumnMetadata.named("STATUS_DESCRIPTION_FI").withIndex(8).ofType(Types.VARCHAR).withSize(255));
        addMetadata(statusDescriptionSv, ColumnMetadata.named("STATUS_DESCRIPTION_SV").withIndex(9).ofType(Types.VARCHAR).withSize(255));
        addMetadata(usageCommercial, ColumnMetadata.named("USAGE_COMMERCIAL").withIndex(34).ofType(Types.BOOLEAN).withSize(1).notNull());
        addMetadata(usageHsl, ColumnMetadata.named("USAGE_HSL").withIndex(33).ofType(Types.BOOLEAN).withSize(1).notNull());
        addMetadata(usageParkAndRide, ColumnMetadata.named("USAGE_PARK_AND_RIDE").withIndex(32).ofType(Types.BOOLEAN).withSize(1).notNull());
    }

}

