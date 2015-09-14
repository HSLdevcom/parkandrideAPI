package fi.hsl.parkandride.back.sql;

import com.mysema.query.sql.ColumnMetadata;
import com.mysema.query.sql.spatial.RelationalPathSpatial;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.DateTimePath;
import com.mysema.query.types.path.EnumPath;
import com.mysema.query.types.path.NumberPath;

import javax.annotation.Generated;
import java.sql.Types;

import static com.mysema.query.types.PathMetadataFactory.forVariable;



/**
 * QFacilityPredictionHistory is a Querydsl query type for QFacilityPredictionHistory
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QFacilityPredictionHistory extends RelationalPathSpatial<QFacilityPredictionHistory> {

    private static final long serialVersionUID = -978140873;

    public static final QFacilityPredictionHistory facilityPredictionHistory = new QFacilityPredictionHistory("FACILITY_PREDICTION_HISTORY");

    public final EnumPath<fi.hsl.parkandride.core.domain.CapacityType> capacityType = createEnum("capacityType", fi.hsl.parkandride.core.domain.CapacityType.class);

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final NumberPath<Integer> forecastDistanceInMinutes = createNumber("forecastDistanceInMinutes", Integer.class);

    public final NumberPath<Integer> spacesAvailable = createNumber("spacesAvailable", Integer.class);

    public final DateTimePath<org.joda.time.DateTime> ts = createDateTime("ts", org.joda.time.DateTime.class);

    public final EnumPath<fi.hsl.parkandride.core.domain.Usage> usage = createEnum("usage", fi.hsl.parkandride.core.domain.Usage.class);

    public final com.mysema.query.sql.PrimaryKey<QFacilityPredictionHistory> constraint34 = createPrimaryKey(capacityType, facilityId, forecastDistanceInMinutes, ts, usage);

    public final com.mysema.query.sql.ForeignKey<QUsage> facilityPredictionHistoryUsageFk = createForeignKey(usage, "NAME");

    public final com.mysema.query.sql.ForeignKey<QCapacityType> facilityPredictionHistoryCapacityTypeFk = createForeignKey(capacityType, "NAME");

    public final com.mysema.query.sql.ForeignKey<QFacility> facilityPredictionHistoryFacilityIdFk = createForeignKey(facilityId, "ID");

    public QFacilityPredictionHistory(String variable) {
        super(QFacilityPredictionHistory.class, forVariable(variable), "PUBLIC", "FACILITY_PREDICTION_HISTORY");
        addMetadata();
    }

    public QFacilityPredictionHistory(String variable, String schema, String table) {
        super(QFacilityPredictionHistory.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFacilityPredictionHistory(Path<? extends QFacilityPredictionHistory> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "FACILITY_PREDICTION_HISTORY");
        addMetadata();
    }

    public QFacilityPredictionHistory(PathMetadata<?> metadata) {
        super(QFacilityPredictionHistory.class, metadata, "PUBLIC", "FACILITY_PREDICTION_HISTORY");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(capacityType, ColumnMetadata.named("CAPACITY_TYPE").withIndex(2).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(facilityId, ColumnMetadata.named("FACILITY_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(forecastDistanceInMinutes, ColumnMetadata.named("FORECAST_DISTANCE_IN_MINUTES").withIndex(5).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(spacesAvailable, ColumnMetadata.named("SPACES_AVAILABLE").withIndex(6).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(ts, ColumnMetadata.named("TS").withIndex(4).ofType(Types.TIMESTAMP).withSize(23).withDigits(10).notNull());
        addMetadata(usage, ColumnMetadata.named("USAGE").withIndex(3).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

