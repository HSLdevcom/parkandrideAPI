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
import org.joda.time.DateTimeZone;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;

import java.util.Optional;
import java.util.stream.Stream;

public class PredictionDao implements PredictionRepository {

    public static final Hours PREDICTION_WINDOW = Hours.hours(24);
    public static final Minutes PREDICTION_RESOLUTION = Minutes.minutes(5);

    private static final QFacilityPrediction qPrediction = QFacilityPrediction.facilityPrediction;

    private final PostgresQueryFactory queryFactory;

    public PredictionDao(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @TransactionalWrite
    @Override
    public void updatePredictions(PredictionBatch pb) {
        DateTime start = toPredictionResolution(pb.sourceTimestamp);
        DateTime end = start.plus(PREDICTION_WINDOW).minus(PREDICTION_RESOLUTION);

        SQLUpdateClause update = queryFactory.update(qPrediction)
                .where(qPrediction.facilityId.eq(pb.facilityId),
                        qPrediction.capacityType.eq(pb.capacityType),
                        qPrediction.usage.eq(pb.usage))
                .set(qPrediction.start, start);

        for (Prediction prediction : pb.predictions) {
            DateTime timestamp = toPredictionResolution(prediction.timestamp);
            if (timestamp.isBefore(start) || timestamp.isAfter(end)) {
                continue;
            }
            update.set(spacesAvailableAt(timestamp), prediction.spacesAvailable);
        }

        long updatedRows = update.execute();
        if (updatedRows == 0) {
            insertBlankPredictionRow(pb);
            updatePredictions(pb);
        }
    }

    private void insertBlankPredictionRow(PredictionBatch pb) {
        queryFactory.insert(qPrediction)
                .set(qPrediction.facilityId, pb.facilityId)
                .set(qPrediction.capacityType, pb.capacityType)
                .set(qPrediction.usage, pb.usage)
                .execute();
    }

    @TransactionalRead
    @Override
    public Optional<Prediction> getPrediction(long facilityId, CapacityType capacityType, Usage usage, DateTime timeWithFullPrecision) {
        DateTime time = toPredictionResolution(timeWithFullPrecision);
        Path<Integer> pSpacesAvailable = spacesAvailableAt(time);
        return Optional.ofNullable(queryFactory
                .from(qPrediction)
                .where(qPrediction.facilityId.eq(facilityId),
                        qPrediction.capacityType.eq(capacityType),
                        qPrediction.usage.eq(usage),
                        qPrediction.start.between(time.minus(PREDICTION_WINDOW).plus(PREDICTION_RESOLUTION), time))
                .singleResult(new MappingProjection<Prediction>(Prediction.class, pSpacesAvailable) {
                    @Override
                    protected Prediction map(Tuple row) {
                        Integer spacesAvailable = row.get(pSpacesAvailable);
                        if (spacesAvailable == null) {
                            return null;
                        }
                        return new Prediction(time, spacesAvailable);
                    }
                }));
    }

    private static Path<Integer> spacesAvailableAt(DateTime timestamp) {
        // Also other parts of this class assume prediction resolution,
        // so we don't do the rounding here, but require the timestamp
        // to already have been properly rounded.
        assert timestamp.equals(toPredictionResolution(timestamp)) : "not in prediction resolution: " + timestamp;

        String hhmm = DateTimeFormat.forPattern("HHmm").print(timestamp.withZone(DateTimeZone.UTC));
        return Stream.of(qPrediction.all())
                .filter(p -> p.getMetadata().getName().equals("spacesAvailableAt" + hhmm))
                .map(PredictionDao::castToIntegerPath)
                .findFirst()
                .get();
    }

    static DateTime toPredictionResolution(DateTime timestamp) {
        return TimeUtil.roundMinutes(PREDICTION_RESOLUTION.getMinutes(), timestamp);
    }

    @SuppressWarnings("unchecked")
    private static Path<Integer> castToIntegerPath(Path<?> path) {
        if (path.getType().equals(Integer.class)) {
            return (Path<Integer>) path;
        }
        throw new ClassCastException(path + " has type " + path.getType());
    }
}
