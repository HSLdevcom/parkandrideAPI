package fi.hsl.parkandride.back.sql;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.spatial.RelationalPathSpatial;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;



/**
 * QFacilityPredictionHistory is a Querydsl query type for QFacilityPredictionHistory
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QFacilityPredictionHistory extends RelationalPathSpatial<QFacilityPredictionHistory> {

    private static final long serialVersionUID = -978140873;

    public static final QFacilityPredictionHistory facilityPredictionHistory = new QFacilityPredictionHistory("FACILITY_PREDICTION_HISTORY");

    public final NumberPath<Integer> forecastDistanceInMinutes = createNumber("forecastDistanceInMinutes", Integer.class);

    public final NumberPath<Long> predictorId = createNumber("predictorId", Long.class);

    public final NumberPath<Integer> spacesAvailable = createNumber("spacesAvailable", Integer.class);

    public final DateTimePath<org.joda.time.DateTime> ts = createDateTime("ts", org.joda.time.DateTime.class);

    public final com.querydsl.sql.PrimaryKey<QFacilityPredictionHistory> constraint34b = createPrimaryKey(forecastDistanceInMinutes, predictorId, ts);

    public final com.querydsl.sql.ForeignKey<QPredictor> facilityPredictionHistoryPredictorIdFk = createForeignKey(predictorId, "ID");

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

    public QFacilityPredictionHistory(PathMetadata metadata) {
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

