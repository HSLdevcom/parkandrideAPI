// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import java.util.ArrayList;
import java.util.List;

public class SameAsLatestPredictor implements Predictor {

    @Override
    public List<Prediction> predict(PredictorState state, UtilizationHistory history) {
        List<Prediction> predictions = new ArrayList<>();
        history.getUpdatesSince(state.latestUtilization)
                .reduce((older, newer) -> newer)
                .ifPresent(lastUpdate -> {
                    state.latestUtilization = lastUpdate.timestamp;
                    predictions.add(new Prediction(lastUpdate.timestamp, lastUpdate.spacesAvailable));
                    predictions.add(new Prediction(lastUpdate.timestamp.plusDays(1), lastUpdate.spacesAvailable));
                });
        return predictions;
    }
}
