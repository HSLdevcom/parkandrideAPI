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
 * QFacilityPredictionHistory is a Querydsl query type for QFacilityPredictionHistory
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QFacilityPredictionHistory extends RelationalPathSpatial<QFacilityPredictionHistory> {

    private static final long serialVersionUID = -978140873;

    public static final QFacilityPredictionHistory facilityPredictionHistory = new QFacilityPredictionHistory("FACILITY_PREDICTION_HISTORY");

    public final NumberPath<Integer> forecastDistanceInMinutes = createNumber("forecastDistanceInMinutes", Integer.class);

    public final NumberPath<Long> predictorId = createNumber("predictorId", Long.class);

    public final NumberPath<Integer> spacesAvailable = createNumber("spacesAvailable", Integer.class);

    public final DateTimePath<org.joda.time.DateTime> ts = createDateTime("ts", org.joda.time.DateTime.class);

    public final com.mysema.query.sql.PrimaryKey<QFacilityPredictionHistory> constraint34b = createPrimaryKey(forecastDistanceInMinutes, predictorId, ts);

    public final com.mysema.query.sql.ForeignKey<QPredictor> facilityPredictionHistoryPredictorIdFk = createForeignKey(predictorId, "ID");

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
        addMetadata(forecastDistanceInMinutes, ColumnMetadata.named("FORECAST_DISTANCE_IN_MINUTES").withIndex(3).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(predictorId, ColumnMetadata.named("PREDICTOR_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(spacesAvailable, ColumnMetadata.named("SPACES_AVAILABLE").withIndex(4).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(ts, ColumnMetadata.named("TS").withIndex(2).ofType(Types.TIMESTAMP).withSize(23).withDigits(10).notNull());
    }

}

