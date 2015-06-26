// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.prediction;

import com.mysema.commons.lang.CloseableIterator;
import fi.hsl.parkandride.core.domain.Utilization;

import java.util.ArrayList;
import java.util.List;

import static java.util.Spliterator.IMMUTABLE;
import static java.util.Spliterator.NONNULL;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;

public class SameAsLatestPredictor implements Predictor {

    public static final String TYPE = "same-as-latest";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public List<Prediction> predict(PredictorState state, UtilizationHistory history) {
        List<Prediction> predictions = new ArrayList<>();
        try (CloseableIterator<Utilization>  utilizations = history.getUpdatesSince(state.latestUtilization)) {
            stream(spliteratorUnknownSize(utilizations, NONNULL | IMMUTABLE), false)
                .reduce((older, newer) -> newer)
                .ifPresent(lastUpdate -> {
                    state.latestUtilization = lastUpdate.timestamp;
                    predictions.add(new Prediction(lastUpdate.timestamp, lastUpdate.spacesAvailable));
                    predictions.add(new Prediction(lastUpdate.timestamp.plusDays(1), lastUpdate.spacesAvailable));
                });
        }
        return predictions;
    }
}
