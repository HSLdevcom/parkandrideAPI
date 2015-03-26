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

        pb.predictions.stream()
                .sorted(Comparator.comparing(p -> p.timestamp))
                .map(toPredictionResolution())
                .collect(groupByTimeKeepingNewest())
                .values().stream()
                .map(Collections::singletonList)
                .reduce(new ArrayList<>(), linearInterpolation()).stream()
                .filter(isWithin(start, end))
                .forEach(p -> update.set(spacesAvailableAt(p.timestamp), p.spacesAvailable));

        long updatedRows = update.execute();
        if (updatedRows == 0) {
            insertBlankPredictionRow(pb);
            updatePredictions(pb);
        }
    }

    private static Predicate<Prediction> isWithin(DateTime start, DateTime end) {
        return p -> !p.timestamp.isBefore(start) && !p.timestamp.isAfter(end);
    }

    private static Function<Prediction, Prediction> toPredictionResolution() {
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
