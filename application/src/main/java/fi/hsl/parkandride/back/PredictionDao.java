// Copyright © 2015 HSL

package fi.hsl.parkandride.back;

import com.mysema.query.Tuple;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.Path;
import fi.hsl.parkandride.back.sql.QFacilityPrediction;
import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.domain.CapacityType;
import fi.hsl.parkandride.core.domain.Prediction;
import fi.hsl.parkandride.core.domain.PredictionBatch;
import fi.hsl.parkandride.core.domain.Usage;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Optional;
import java.util.stream.Stream;

public class PredictionDao implements PredictionRepository {

    private static final QFacilityPrediction qPrediction = QFacilityPrediction.facilityPrediction;

    private final PostgresQueryFactory queryFactory;

    public PredictionDao(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @TransactionalWrite
    @Override
    public void updatePredictions(PredictionBatch batch) {
        SQLUpdateClause update = queryFactory.update(qPrediction)
                .where(qPrediction.facilityId.eq(batch.facilityId));

        for (Prediction prediction : batch.predictions) {
            update.set(spacesAvailableAt(prediction.timestamp), prediction.spacesAvailable);
        }

        long updatedRows = update.execute();
        if (updatedRows == 0) {
            insertBlankPredictionRow(batch);
            updatePredictions(batch);
        }
    }

    private void insertBlankPredictionRow(PredictionBatch batch) {
        queryFactory.insert(qPrediction)
                .set(qPrediction.facilityId, batch.facilityId)
                .set(qPrediction.capacityType, batch.capacityType)
                .set(qPrediction.usage, batch.usage)
                .execute();
    }

    @TransactionalRead
    @Override
    public Optional<Prediction> getPrediction(long facilityId, CapacityType capacityType, Usage usage, DateTime target) {
        DateTime timestamp = toPredictionResolution(target);
        Path<Integer> spacesAvailable = spacesAvailableAt(target);
        return Optional.ofNullable(queryFactory.from(qPrediction).singleResult(new MappingProjection<Prediction>(Prediction.class, spacesAvailable) {
            @Override
            protected Prediction map(Tuple row) {
                // TODO: handle null values for spaces available
                // TODO: handle outdated predictions
                return new Prediction(
                        timestamp,
                        row.get(spacesAvailable)
                );
            }
        }));
    }

    private static Path<Integer> spacesAvailableAt(DateTime timestamp) {
        // TODO: use UTC
        String hhmm = DateTimeFormat.forPattern("HHmm").print(toPredictionResolution(timestamp));
        return Stream.of(qPrediction.all())
                .filter(p -> p.getMetadata().getName().equals("spacesAvailableAt" + hhmm))
                .map(PredictionDao::castToIntegerPath)
                .findFirst()
                .get();
    }

    private static DateTime toPredictionResolution(DateTime timestamp) {
        return TimeUtil.roundMinutes(5, timestamp);
    }

    @SuppressWarnings("unchecked")
    private static Path<Integer> castToIntegerPath(Path<?> path) {
        if (path.getType().equals(Integer.class)) {
            return (Path<Integer>) path;
        }
        throw new ClassCastException(path + " has type " + path.getType());
    }
}
