// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

public class SameAsLatestPredictor implements Predictor {

    @Override
    public void predict(PredictorState state, PredictionBatch batch, UtilizationHistory history) {
        history.getUpdatesSince(state.latestProcessed)
                .reduce((older, newer) -> newer)
                .ifPresent(lastUpdate -> {
                    state.latestProcessed = lastUpdate.timestamp;
                    batch.predictions.add(new Prediction(lastUpdate.timestamp, lastUpdate.spacesAvailable));
                    batch.predictions.add(new Prediction(lastUpdate.timestamp.plusDays(1), lastUpdate.spacesAvailable));
                });
    }
}
