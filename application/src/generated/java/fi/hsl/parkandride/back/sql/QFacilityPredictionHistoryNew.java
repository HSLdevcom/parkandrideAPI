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
 * QFacilityPredictionHistoryNew is a Querydsl query type for QFacilityPredictionHistoryNew
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QFacilityPredictionHistoryNew extends RelationalPathSpatial<QFacilityPredictionHistoryNew> {

    private static final long serialVersionUID = 1558434025;

    public static final QFacilityPredictionHistoryNew facilityPredictionHistoryNew = new QFacilityPredictionHistoryNew("FACILITY_PREDICTION_HISTORY_NEW");

    public final NumberPath<Integer> forecastDistanceInMinutes = createNumber("forecastDistanceInMinutes", Integer.class);

    public final NumberPath<Long> predictorId = createNumber("predictorId", Long.class);

    public final NumberPath<Integer> spacesAvailable = createNumber("spacesAvailable", Integer.class);

    public final DateTimePath<org.joda.time.DateTime> ts = createDateTime("ts", org.joda.time.DateTime.class);

    public final com.mysema.query.sql.PrimaryKey<QFacilityPredictionHistoryNew> constraint2e = createPrimaryKey(forecastDistanceInMinutes, predictorId, ts);

    public final com.mysema.query.sql.ForeignKey<QPredictor> facilityPredictionHistoryPredictorIdFk = createForeignKey(predictorId, "ID");

    public QFacilityPredictionHistoryNew(String variable) {
        super(QFacilityPredictionHistoryNew.class, forVariable(variable), "PUBLIC", "FACILITY_PREDICTION_HISTORY_NEW");
        addMetadata();
    }

    public QFacilityPredictionHistoryNew(String variable, String schema, String table) {
        super(QFacilityPredictionHistoryNew.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFacilityPredictionHistoryNew(Path<? extends QFacilityPredictionHistoryNew> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "FACILITY_PREDICTION_HISTORY_NEW");
        addMetadata();
    }

    public QFacilityPredictionHistoryNew(PathMetadata<?> metadata) {
        super(QFacilityPredictionHistoryNew.class, metadata, "PUBLIC", "FACILITY_PREDICTION_HISTORY_NEW");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(forecastDistanceInMinutes, ColumnMetadata.named("FORECAST_DISTANCE_IN_MINUTES").withIndex(3).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(predictorId, ColumnMetadata.named("PREDICTOR_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(spacesAvailable, ColumnMetadata.named("SPACES_AVAILABLE").withIndex(4).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(ts, ColumnMetadata.named("TS").withIndex(2).ofType(Types.TIMESTAMP).withSize(23).withDigits(10).notNull());
    }

}

