// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.prediction;

import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.domain.Utilization;
import org.joda.time.DateTime;
import org.joda.time.Weeks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AverageOfPreviousWeeksPredictor implements Predictor {

    public static final String TYPE = "average-of-previous-weeks";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public List<Prediction> predict(PredictorState state, UtilizationHistory history) {
        Utilization latest = history.getLatest();
        DateTime now = latest.timestamp;

        List<List<Utilization>> groupedByWeek = Stream.of(Weeks.weeks(1), Weeks.weeks(2), Weeks.weeks(3))
                .map(weeks -> {
                    DateTime start = now.minus(weeks);
                    DateTime end = start.plus(PredictionRepository.PREDICTION_WINDOW);
                    return history.getRange(start, end);
                    // TODO: make timestamps relative to start
                })
                .collect(Collectors.toList());

        List<List<Utilization>> groupedByTimeOfDay = transpose(groupedByWeek);

        return groupedByTimeOfDay.stream()
                .map(this::toPrediction)
                .collect(Collectors.toList());
    }

    private Prediction toPrediction(List<Utilization> utilizations) {
        DateTime timestamp = utilizations.get(0).timestamp.plusWeeks(1); // XXX: shift time correctly
        int spacesAvailable = (int) Math.round(utilizations.stream()
                .mapToInt(u -> u.spacesAvailable)
                .average()
                .getAsDouble());
        return new Prediction(timestamp, spacesAvailable);
    }

    private static List<List<Utilization>> transpose(List<List<Utilization>> sources) {
        List<Iterator<Utilization>> iterators = sources.stream()
                .map(List::iterator)
                .collect(Collectors.toList());
        List<List<Utilization>> results = new ArrayList<>();
        while (hasNexts(iterators)) {
            results.add(nexts(iterators));
        }
        return results;
    }

    private static boolean hasNexts(List<Iterator<Utilization>> heads) {
        return heads.stream().anyMatch(Iterator::hasNext);
    }

    private static List<Utilization> nexts(List<Iterator<Utilization>> heads) {
        return heads.stream()
                .filter(Iterator::hasNext)
                .map(Iterator::next)
                .collect(Collectors.toList());
    }
}
