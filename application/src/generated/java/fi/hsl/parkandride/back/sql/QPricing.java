package fi.hsl.parkandride.back.sql;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.spatial.RelationalPathSpatial;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;



/**
 * QPricing is a Querydsl query type for QPricing
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QPricing extends RelationalPathSpatial<QPricing> {

    private static final long serialVersionUID = 584432539;

    public static final QPricing pricing = new QPricing("PRICING");

    public final EnumPath<fi.hsl.parkandride.core.domain.CapacityType> capacityType = createEnum("capacityType", fi.hsl.parkandride.core.domain.CapacityType.class);

    public final EnumPath<fi.hsl.parkandride.core.domain.DayType> dayType = createEnum("dayType", fi.hsl.parkandride.core.domain.DayType.class);

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final NumberPath<fi.hsl.parkandride.core.domain.Time> fromTime = createNumber("fromTime", fi.hsl.parkandride.core.domain.Time.class);

    public final NumberPath<Integer> maxCapacity = createNumber("maxCapacity", Integer.class);

    public final StringPath priceEn = createString("priceEn");

    public final StringPath priceFi = createString("priceFi");

    public final StringPath priceSv = createString("priceSv");

    public final NumberPath<fi.hsl.parkandride.core.domain.Time> untilTime = createNumber("untilTime", fi.hsl.parkandride.core.domain.Time.class);

    public final EnumPath<fi.hsl.parkandride.core.domain.Usage> usage = createEnum("usage", fi.hsl.parkandride.core.domain.Usage.class);

    public final com.querydsl.sql.PrimaryKey<QPricing> constraint18 = createPrimaryKey(capacityType, dayType, facilityId, fromTime, usage);

    public final com.querydsl.sql.ForeignKey<QUsage> pricingUsageFk = createForeignKey(usage, "NAME");

    public final com.querydsl.sql.ForeignKey<QCapacityType> pricingCapacityTypeFk = createForeignKey(capacityType, "NAME");

    public final com.querydsl.sql.ForeignKey<QFacility> pricingFacilityIdFk = createForeignKey(facilityId, "ID");

    public final com.querydsl.sql.ForeignKey<QDayType> pricingDayTypeFk = createForeignKey(dayType, "NAME");

    public QPricing(String variable) {
        super(QPricing.class, forVariable(variable), "PUBLIC", "PRICING");
        addMetadata();
    }

    public QPricing(String variable, String schema, String table) {
        super(QPricing.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPricing(Path<? extends QPricing> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "PRICING");
        addMetadata();
    }

    public QPricing(PathMetadata metadata) {
        super(QPricing.class, metadata, "PUBLIC", "PRICING");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(capacityType, ColumnMetadata.named("CAPACITY_TYPE").withIndex(2).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(dayType, ColumnMetadata.named("DAY_TYPE").withIndex(5).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(facilityId, ColumnMetadata.named("FACILITY_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(fromTime, ColumnMetadata.named("FROM_TIME").withIndex(6).ofType(Types.SMALLINT).withSize(5).notNull());
        addMetadata(maxCapacity, ColumnMetadata.named("MAX_CAPACITY").withIndex(4).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(priceEn, ColumnMetadata.named("PRICE_EN").withIndex(10).ofType(Types.VARCHAR).withSize(255));
        addMetadata(priceFi, ColumnMetadata.named("PRICE_FI").withIndex(8).ofType(Types.VARCHAR).withSize(255));
        addMetadata(priceSv, ColumnMetadata.named("PRICE_SV").withIndex(9).ofType(Types.VARCHAR).withSize(255));
        addMetadata(untilTime, ColumnMetadata.named("UNTIL_TIME").withIndex(7).ofType(Types.SMALLINT).withSize(5).notNull());
        addMetadata(usage, ColumnMetadata.named("USAGE").withIndex(3).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

