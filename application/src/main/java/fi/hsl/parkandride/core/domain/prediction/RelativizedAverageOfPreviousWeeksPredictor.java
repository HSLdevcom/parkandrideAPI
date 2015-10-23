// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.prediction;

import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.domain.Utilization;
import org.joda.time.DateTime;
import org.joda.time.ReadablePeriod;
import org.joda.time.Weeks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class RelativizedAverageOfPreviousWeeksPredictor implements Predictor {

    private static final Logger log = LoggerFactory.getLogger(RelativizedAverageOfPreviousWeeksPredictor.class);
    private static final List<ReadablePeriod> LOOKBACK_PERIODS = Arrays.asList(Weeks.weeks(1), Weeks.weeks(2), Weeks.weeks(3));

    public static final String TYPE = "relative-average-of-previous-weeks";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public List<Prediction> predict(PredictorState state, UtilizationHistory history) {
        Utilization latest = history.getLatest();
        DateTime now = state.latestUtilization = latest.timestamp;

        final UtilizationHistory inMemoryHistory = new UtilizationHistoryList(history.getRange(now.minusWeeks(3), now));

        List<List<Prediction>> groupedByWeek =
                LOOKBACK_PERIODS.stream()
                .map(offset -> {
                    DateTime start = now.minus(offset);
                    DateTime end = start.plus(PredictionRepository.PREDICTION_WINDOW);
                    List<Utilization> utilizations = inMemoryHistory.getRange(start, end);
                    return utilizations.stream()
                            .map(u -> new Prediction(u.timestamp.plus(offset), u.spacesAvailable))
                            .collect(Collectors.toList());
                })
                .collect(Collectors.toList());

        List<List<Prediction>> groupedByTimeOfDay = transpose(groupedByWeek);

        return groupedByTimeOfDay.stream()
                .map(this::reduce)
                .collect(Collectors.toList());
    }

    private Prediction reduce(List<Prediction> predictions) {
        DateTime timestamp = predictions.get(0).timestamp;
        if (!predictions.stream()
                .map(p -> p.timestamp)
                .allMatch(timestamp::equals)) {
            log.warn("Something went wrong. Not all predictions have the same timestamp: {}", predictions);
        }
        int spacesAvailable = (int) Math.round(predictions.stream()
                .mapToInt(u -> u.spacesAvailable)
                .average()
                .getAsDouble());
        return new Prediction(timestamp, spacesAvailable);
    }

    private static <T> List<List<T>> transpose(List<List<T>> sources) {
        List<Iterator<T>> iterators = sources.stream()
                .map(List::iterator)
                .collect(Collectors.toList());
        List<List<T>> results = new ArrayList<>();
        while (hasNexts(iterators)) {
            results.add(nexts(iterators));
        }
        return results;
    }

    private static <T> boolean hasNexts(List<Iterator<T>> heads) {
        return heads.stream().anyMatch(Iterator::hasNext);
    }

    private static <T> List<T> nexts(List<Iterator<T>> heads) {
        return heads.stream()
                .filter(Iterator::hasNext)
                .map(Iterator::next)
                .collect(Collectors.toList());
    }
}
