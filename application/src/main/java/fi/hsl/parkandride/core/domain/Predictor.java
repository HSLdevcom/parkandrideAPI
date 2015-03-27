// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

public interface Predictor {

    void predict(PredictorState state, PredictionBatch batch, UtilizationHistory history);
}
