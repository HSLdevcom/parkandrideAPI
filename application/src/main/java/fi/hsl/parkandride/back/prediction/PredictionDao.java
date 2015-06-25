// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back.prediction;

import com.mysema.query.Tuple;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.Path;
import com.mysema.query.types.expr.BooleanExpression;
import fi.hsl.parkandride.back.TimeUtil;
import fi.hsl.parkandride.back.sql.QFacilityPrediction;
import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.domain.UtilizationKey;
import fi.hsl.parkandride.core.domain.prediction.Prediction;
import fi.hsl.parkandride.core.domain.prediction.PredictionBatch;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.core.service.ValidationService;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PredictionDao implements PredictionRepository {

    public static final Hours PREDICTION_WINDOW = Hours.hours(24);
    public static final Minutes PREDICTION_RESOLUTION = Minutes.minutes(5);

    private static final QFacilityPrediction qPrediction = QFacilityPrediction.facilityPrediction;
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
    public void updatePredictions(PredictionBatch pb) {
        validationService.validate(pb);
        DateTime start = toPredictionResolution(pb.sourceTimestamp);
        DateTime end = start.plus(PREDICTION_WINDOW).minus(PREDICTION_RESOLUTION);

        SQLUpdateClause update = queryFactory.update(qPrediction)
                .where(qPrediction.facilityId.eq(pb.utilizationKey.facilityId),
                        qPrediction.capacityType.eq(pb.utilizationKey.capacityType),
                        qPrediction.usage.eq(pb.utilizationKey.usage))
                .set(qPrediction.start, start);

        pb.predictions.stream()
                .sorted(Comparator.comparing(p -> p.timestamp))
                .map(roundTimestampsToPredictionResolution())
                .collect(groupByTimeKeepingNewest()) // -> Map<DateTime, Prediction>
                .values().stream()
                .map(Collections::singletonList)                            // 1. wrap values in immutable singleton lists
                .reduce(new ArrayList<>(), linearInterpolation()).stream()  // 2. mutable ArrayList as accumulator
                .filter(isWithin(start, end)) // after interpolation because of PredictionDaoTest.does_linear_interpolation_also_between_values_outside_the_prediction_window
                .forEach(p -> update.set(spacesAvailableAt(p.timestamp), p.spacesAvailable));

        long updatedRows = update.execute();
        if (updatedRows == 0) {
            insertBlankPredictionRow(pb);
            updatePredictions(pb);
        }
    }

    private void insertBlankPredictionRow(PredictionBatch pb) {
        queryFactory.insert(qPrediction)
                .set(qPrediction.facilityId, pb.utilizationKey.facilityId)
                .set(qPrediction.capacityType, pb.utilizationKey.capacityType)
                .set(qPrediction.usage, pb.utilizationKey.usage)
                .execute();
    }

    private static Predicate<Prediction> isWithin(DateTime start, DateTime end) {
        return p -> !p.timestamp.isBefore(start) && !p.timestamp.isAfter(end);
    }

    private static Function<Prediction, Prediction> roundTimestampsToPredictionResolution() {
        return p -> new Prediction(toPredictionResolution(p.timestamp), p.spacesAvailable);
    }

    private static Collector<Prediction, ?, TreeMap<DateTime, Prediction>> groupByTimeKeepingNewest() {
        return Collectors.toMap(
                p -> p.timestamp,
                Function.identity(),
                (a, b) -> a.timestamp.isAfter(b.timestamp) ? a : b,
                TreeMap::new
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
