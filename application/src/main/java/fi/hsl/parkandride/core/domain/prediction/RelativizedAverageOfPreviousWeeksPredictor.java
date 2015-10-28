// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.prediction;

import fi.hsl.parkandride.back.ListUtil;
import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.domain.Utilization;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.ReadablePeriod;
import org.joda.time.Weeks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class RelativizedAverageOfPreviousWeeksPredictor implements Predictor {

    private static final Logger log = LoggerFactory.getLogger(RelativizedAverageOfPreviousWeeksPredictor.class);

    public static final List<ReadablePeriod> LOOKBACK_PERIODS = Arrays.asList(Weeks.weeks(1), Weeks.weeks(2), Weeks.weeks(3));
    public static final Minutes LOOKBACK_MINUTES = Minutes.minutes(120);

    public static final String TYPE = "relative-average-of-previous-weeks";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public List<Prediction> predict(PredictorState state, UtilizationHistory history) {
        Optional<Utilization> latest = history.getLatest();
        if (!latest.isPresent()) {
            return Collections.emptyList();
        }
        DateTime now = state.latestUtilization = latest.get().timestamp;
        final UtilizationHistory inMemoryHistory = new UtilizationHistoryList(history.getRange(now.minusWeeks(3).minus(LOOKBACK_MINUTES), now));

        List<List<Prediction>> groupedByWeek =
                LOOKBACK_PERIODS.stream()
                        .map(offset -> {
                            offset.toMutablePeriod().add(PredictionRepository.PREDICTION_WINDOW);
                            DateTime start = now.minus(offset);
                            DateTime end = start.plus(PredictionRepository.PREDICTION_WINDOW);
                            Optional<Utilization> utilizationAtReferenceTime = inMemoryHistory.getAt(start);
                            if (!utilizationAtReferenceTime.isPresent()) {
                                return null;
                            }

                            Integer spacesAvailableAtReferenceTime = utilizationAtReferenceTime.get().spacesAvailable;
                            List<Utilization> utilizations = inMemoryHistory.getRange(start, end);
                            return utilizations.stream()
                                    .map(u -> new Prediction(u.timestamp.plus(offset), u.spacesAvailable - spacesAvailableAtReferenceTime))
                                    .collect(Collectors.toList());
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        List<List<Prediction>> groupedByTimeOfDay = ListUtil.transpose(groupedByWeek);

        return groupedByTimeOfDay.stream()
                .map(predictions -> reduce(predictions, latest.get().spacesAvailable, getUtilizationMultiplier(now, inMemoryHistory)))
                .collect(Collectors.toList());
    }

    private Double getUtilizationMultiplier(DateTime now, UtilizationHistory inMemoryHistory) {
        final List<Utilization> recentUtilizations = inMemoryHistory.getRange(now.minus(LOOKBACK_MINUTES), now);
        Double recentUtilizationArea = Math.max(1, calculateAreaAverageByDataPoints(recentUtilizations));
        Double referenceUtilizationAreaAverage = Math.max(1,
                LOOKBACK_PERIODS.stream()
                        .map(offset -> now.minus(offset))
                        .map(referenceTime -> inMemoryHistory.getRange(referenceTime.minus(LOOKBACK_MINUTES), referenceTime))
                        .filter(utilizationList -> !utilizationList.isEmpty())
                        .mapToDouble(utilizationList -> calculateAreaAverageByDataPoints(utilizationList))
                        .average()
                        .orElseGet(() -> recentUtilizationArea));
        return Math.max(1, recentUtilizationArea / referenceUtilizationAreaAverage);
    }

    private double calculateAreaAverageByDataPoints(List<Utilization> utilizationList) {
        int referenceSpaces = utilizationList.get(utilizationList.size() - 1).spacesAvailable;
        return utilizationList.stream()
                .mapToDouble(u -> Math.abs(u.spacesAvailable - referenceSpaces)).average().getAsDouble();
    }

    private Prediction reduce(List<Prediction> predictions, int spacesAvailableCorrection, double utilizationMultiplier) {
        DateTime timestamp = predictions.get(0).timestamp;
        if (!predictions.stream()
                .map(p -> p.timestamp)
                .allMatch(timestamp::equals)) {
            log.warn("Something went wrong. Not all predictions have the same timestamp: {}", predictions);
        }
        int spacesAvailable = (int) Math.round(utilizationMultiplier * predictions.stream()
                .mapToDouble(u -> u.spacesAvailable)
                .average()
                .getAsDouble());
        final int predictedSpacesAvailable = Math.max(0, spacesAvailable + spacesAvailableCorrection);
        return new Prediction(timestamp, predictedSpacesAvailable);
    }
}
