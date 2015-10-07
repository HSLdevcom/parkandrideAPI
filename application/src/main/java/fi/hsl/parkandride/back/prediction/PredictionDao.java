// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back.prediction;

import com.mysema.query.Tuple;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.Expression;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.Path;
import com.mysema.query.types.Projections;
import com.mysema.query.types.expr.BooleanExpression;
import fi.hsl.parkandride.back.TimeUtil;
import fi.hsl.parkandride.back.sql.QFacilityPrediction;
import fi.hsl.parkandride.back.sql.QFacilityPredictionHistory;
import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.domain.UtilizationKey;
import fi.hsl.parkandride.core.domain.prediction.Prediction;
import fi.hsl.parkandride.core.domain.prediction.PredictionBatch;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.core.service.ValidationService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class PredictionDao implements PredictionRepository {

    private static final QFacilityPrediction qPrediction = QFacilityPrediction.facilityPrediction;
    private static final QFacilityPredictionHistory qPredictionHistory = QFacilityPredictionHistory.facilityPredictionHistory;
    private static final Map<String, Path<Integer>> spacesAvailableColumnsByHHmm = Collections.unmodifiableMap(
            Stream.of(qPrediction.all())
                    .filter(p -> p.getMetadata().getName().startsWith("spacesAvailableAt"))
                    .map(PredictionDao::castToIntegerPath)
                    .collect(Collectors.toMap(
                            p -> p.getMetadata().getName().substring("spacesAvailableAt".length()),
                            Function.identity())));

    private final PostgresQueryFactory queryFactory;
    private final ValidationService validationService;

    public PredictionDao(PostgresQueryFactory queryFactory, ValidationService validationService) {
        this.queryFactory = queryFactory;
        this.validationService = validationService;
    }

    @TransactionalWrite
    @Override
    public void updatePredictions(PredictionBatch pb, Long predictorId) {
        validationService.validate(pb);
        UtilizationKey utilizationKey = pb.utilizationKey;
        DateTime start = toPredictionResolution(pb.sourceTimestamp);
        List<Prediction> predictions = normalizeToPredictionWindow(start, pb.predictions);

        long updatedRows = maybeUpdatePredictionLookupTable(utilizationKey, start, predictions);
        if (updatedRows == 0) {
            initializePredictionLookupTable(utilizationKey);
            updatePredictions(pb, predictorId); // retry now that the lookup table exists
        } else {
            savePredictionHistory(predictorId, start, predictions);
        }
    }

    private static List<Prediction> normalizeToPredictionWindow(DateTime start, List<Prediction> predictions) {
        DateTime end = start.plus(PREDICTION_WINDOW).minus(PREDICTION_RESOLUTION);
        return predictions.stream()
                // remove too fine-grained predictions
                .collect(groupByRoundedTimeKeepingNewest()) // -> Map<DateTime, Prediction>
                .values().stream()
                        // normalize resolution
                .map(roundTimestampsToPredictionResolution())
                        // interpolate too coarse-grained predictions
                .sorted(Comparator.comparing(p -> p.timestamp))
                .map(Collections::singletonList)                            // 1. wrap values in immutable singleton lists
                .reduce(new ArrayList<>(), linearInterpolation()).stream()  // 2. mutable ArrayList as accumulator
                        // normalize range
                .filter(isWithin(start, end)) // after interpolation because of PredictionDaoTest.does_linear_interpolation_also_between_values_outside_the_prediction_window
                .collect(toList());
    }

    private static Predicate<Prediction> isWithin(DateTime start, DateTime end) {
        return p -> !p.timestamp.isBefore(start) && !p.timestamp.isAfter(end);
    }

    private static Function<Prediction, Prediction> roundTimestampsToPredictionResolution() {
        return p -> new Prediction(toPredictionResolution(p.timestamp), p.spacesAvailable);
    }

    private static Collector<Prediction, ?, Map<DateTime, Prediction>> groupByRoundedTimeKeepingNewest() {
        return Collectors.toMap(
                p -> toPredictionResolution(p.timestamp),
                Function.identity(),
                (a, b) -> a.timestamp.isAfter(b.timestamp) ? a : b,
                HashMap::new
        );
    }

    private static BinaryOperator<List<Prediction>> linearInterpolation() {
        return (interpolated, input) -> {
            if (input.size() != 1) {
                throw new IllegalArgumentException("expected one element, but got " + input);
            }
            if (interpolated.isEmpty()) {
                interpolated.addAll(input);
                return interpolated;
            }
            Prediction previous = interpolated.get(interpolated.size() - 1);
            Prediction next = input.get(0);
            for (DateTime timestamp = previous.timestamp.plus(PREDICTION_RESOLUTION);
                 timestamp.isBefore(next.timestamp);
                 timestamp = timestamp.plus(PREDICTION_RESOLUTION)) {
                double totalDuration = new Duration(previous.timestamp, next.timestamp).getMillis();
                double currentDuration = new Duration(previous.timestamp, timestamp).getMillis();
                double proportion = currentDuration / totalDuration;
                int totalChange = next.spacesAvailable - previous.spacesAvailable;
                int currentChange = (int) Math.round(totalChange * proportion);
                int spacesAvailable = previous.spacesAvailable + currentChange;
                interpolated.add(new Prediction(timestamp, spacesAvailable));
            }
            interpolated.add(next);
            return interpolated;
        };
    }

    private void initializePredictionLookupTable(UtilizationKey utilizationKey) {
        queryFactory.insert(qPrediction)
                .set(qPrediction.facilityId, utilizationKey.facilityId)
                .set(qPrediction.capacityType, utilizationKey.capacityType)
                .set(qPrediction.usage, utilizationKey.usage)
                .execute();
    }

    private long maybeUpdatePredictionLookupTable(UtilizationKey utilizationKey, DateTime start, List<Prediction> predictions) {
        SQLUpdateClause update = queryFactory.update(qPrediction)
                .where(qPrediction.facilityId.eq(utilizationKey.facilityId),
                        qPrediction.capacityType.eq(utilizationKey.capacityType),
                        qPrediction.usage.eq(utilizationKey.usage))
                .set(qPrediction.start, start);
        predictions.forEach(p -> update.set(spacesAvailableAt(p.timestamp), p.spacesAvailable));

        return update.execute();
    }

    private void savePredictionHistory(Long predictorId, DateTime start, List<Prediction> predictions) {
        if (!predictions.isEmpty()) {
            final SQLInsertClause insert = queryFactory.insert(qPredictionHistory);
            predictions.forEach(p -> insert
                    .set(qPredictionHistory.predictorId, predictorId)
                    .set(qPredictionHistory.forecastDistanceInMinutes, ((int) new Duration(start, p.timestamp).getStandardMinutes()))
                    .set(qPredictionHistory.ts, p.timestamp)
                    .set(qPredictionHistory.spacesAvailable, p.spacesAvailable)
                    .addBatch());
            insert.execute();
        }
    }

    @TransactionalRead
    @Override
    public Optional<PredictionBatch> getPrediction(UtilizationKey utilizationKey, DateTime time) {
        return asOptional(queryFactory
                .from(qPrediction)
                .where(qPrediction.facilityId.eq(utilizationKey.facilityId),
                        qPrediction.capacityType.eq(utilizationKey.capacityType),
                        qPrediction.usage.eq(utilizationKey.usage))
                .where(isWithinPredictionWindow(time))
                .singleResult(predictionMapping(time)));
    }

    @TransactionalRead
    @Override
    public List<PredictionBatch> getPredictionsByFacility(Long facilityId, DateTime time) {
        return queryFactory
                .from(qPrediction)
                .where(qPrediction.facilityId.eq(facilityId))
                .where(isWithinPredictionWindow(time))
                .list(predictionMapping(time));
    }

    @TransactionalRead
    @Override
    public List<Prediction> getPredictionHistoryByPredictor(Long predictorId, DateTime start, DateTime end, int forecastDistanceInMinutes) {
        return queryFactory.from(qPredictionHistory)
                .where(qPredictionHistory.predictorId.eq(predictorId),
                        qPredictionHistory.forecastDistanceInMinutes.eq(forecastDistanceInMinutes),
                        qPredictionHistory.ts.between(start, end))
                .orderBy(qPredictionHistory.ts.asc())
                .list(historyToPredictionMapping());
    }

    private Expression<Prediction> historyToPredictionMapping() {
        return Projections.constructor(Prediction.class, qPredictionHistory.ts, qPredictionHistory.spacesAvailable);
    }

    private static BooleanExpression isWithinPredictionWindow(DateTime time) {
        time = toPredictionResolution(time);
        return qPrediction.start.between(time.minus(PREDICTION_WINDOW).plus(PREDICTION_RESOLUTION), time);
    }

    private static MappingProjection<PredictionBatch> predictionMapping(DateTime timeWithFullPrecision) {
        DateTime time = toPredictionResolution(timeWithFullPrecision);
        Path<Integer> spacesAvailableColumn = spacesAvailableAt(time);
        return new MappingProjection<PredictionBatch>(PredictionBatch.class,
                qPrediction.facilityId,
                qPrediction.capacityType,
                qPrediction.usage,
                qPrediction.start,
                spacesAvailableColumn) {
            @Override
            protected PredictionBatch map(Tuple row) {
                PredictionBatch pb = new PredictionBatch();
                pb.utilizationKey = new UtilizationKey(
                        row.get(qPrediction.facilityId),
                        row.get(qPrediction.capacityType),
                        row.get(qPrediction.usage)
                );
                pb.sourceTimestamp = row.get(qPrediction.start);
                Integer spacesAvailable = row.get(spacesAvailableColumn);
                if (spacesAvailable != null) {
                    pb.predictions.add(new Prediction(time, spacesAvailable));
                }
                return pb;
            }
        };
    }

    private static Path<Integer> spacesAvailableAt(DateTime timestamp) {
        // Also other parts of this class assume prediction resolution,
        // so we don't do the rounding here, but require the timestamp
        // to already have been properly rounded.
        assert timestamp.equals(toPredictionResolution(timestamp)) : "not in prediction resolution: " + timestamp;

        String hhmm = DateTimeFormat.forPattern("HHmm").print(timestamp.withZone(DateTimeZone.UTC));
        return spacesAvailableColumnsByHHmm.get(hhmm);
    }

    static DateTime toPredictionResolution(DateTime time) {
        return TimeUtil.roundMinutes(PREDICTION_RESOLUTION.getMinutes(), time);
    }

    private static Optional<PredictionBatch> asOptional(PredictionBatch pb) {
        if (pb == null || pb.predictions.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(pb);
        }
    }

    @SuppressWarnings("unchecked")
    private static Path<Integer> castToIntegerPath(Path<?> path) {
        if (path.getType().equals(Integer.class)) {
            return (Path<Integer>) path;
        }
        throw new ClassCastException(path + " has type " + path.getType());
    }
}
