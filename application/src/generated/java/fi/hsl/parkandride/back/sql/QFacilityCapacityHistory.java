package fi.hsl.parkandride.back.sql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;

import com.querydsl.sql.spatial.RelationalPathSpatial;

import com.querydsl.spatial.*;



/**
 * QFacilityCapacityHistory is a Querydsl query type for QFacilityCapacityHistory
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QFacilityCapacityHistory extends RelationalPathSpatial<QFacilityCapacityHistory> {

    private static final long serialVersionUID = 1636246316;

    public static final QFacilityCapacityHistory facilityCapacityHistory = new QFacilityCapacityHistory("FACILITY_CAPACITY_HISTORY");

    public final NumberPath<Integer> capacityBicycle = createNumber("capacityBicycle", Integer.class);

    public final NumberPath<Integer> capacityBicycleSecureSpace = createNumber("capacityBicycleSecureSpace", Integer.class);

    public final NumberPath<Integer> capacityCar = createNumber("capacityCar", Integer.class);

    public final NumberPath<Integer> capacityDisabled = createNumber("capacityDisabled", Integer.class);

    public final NumberPath<Integer> capacityElectricCar = createNumber("capacityElectricCar", Integer.class);

    public final NumberPath<Integer> capacityMotorcycle = createNumber("capacityMotorcycle", Integer.class);

    public final DateTimePath<org.joda.time.DateTime> endTs = createDateTime("endTs", org.joda.time.DateTime.class);

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<org.joda.time.DateTime> startTs = createDateTime("startTs", org.joda.time.DateTime.class);

    public final com.querydsl.sql.PrimaryKey<QFacilityCapacityHistory> constraint8e = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<QFacility> capacityHistoryFacilityIdFk = createForeignKey(facilityId, "ID");

    public QFacilityCapacityHistory(String variable) {
        super(QFacilityCapacityHistory.class, forVariable(variable), "PUBLIC", "FACILITY_CAPACITY_HISTORY");
        addMetadata();
    }

    public QFacilityCapacityHistory(String variable, String schema, String table) {
        super(QFacilityCapacityHistory.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFacilityCapacityHistory(Path<? extends QFacilityCapacityHistory> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "FACILITY_CAPACITY_HISTORY");
        addMetadata();
    }

    public QFacilityCapacityHistory(PathMetadata metadata) {
        super(QFacilityCapacityHistory.class, metadata, "PUBLIC", "FACILITY_CAPACITY_HISTORY");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(capacityBicycle, ColumnMetadata.named("CAPACITY_BICYCLE").withIndex(9).ofType(Types.INTEGER).withSize(10));
        addMetadata(capacityBicycleSecureSpace, ColumnMetadata.named("CAPACITY_BICYCLE_SECURE_SPACE").withIndex(10).ofType(Types.INTEGER).withSize(10));
        addMetadata(capacityCar, ColumnMetadata.named("CAPACITY_CAR").withIndex(5).ofType(Types.INTEGER).withSize(10));
        addMetadata(capacityDisabled, ColumnMetadata.named("CAPACITY_DISABLED").withIndex(6).ofType(Types.INTEGER).withSize(10));
        addMetadata(capacityElectricCar, ColumnMetadata.named("CAPACITY_ELECTRIC_CAR").withIndex(7).ofType(Types.INTEGER).withSize(10));
        addMetadata(capacityMotorcycle, ColumnMetadata.named("CAPACITY_MOTORCYCLE").withIndex(8).ofType(Types.INTEGER).withSize(10));
        addMetadata(endTs, ColumnMetadata.named("END_TS").withIndex(4).ofType(Types.TIMESTAMP).withSize(23).withDigits(10));
        addMetadata(facilityId, ColumnMetadata.named("FACILITY_ID").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(startTs, ColumnMetadata.named("START_TS").withIndex(3).ofType(Types.TIMESTAMP).withSize(23).withDigits(10).notNull());
    }

}

