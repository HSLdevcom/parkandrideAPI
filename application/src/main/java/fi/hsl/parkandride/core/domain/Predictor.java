// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import java.util.List;

public interface Predictor {

    List<Prediction> predict(PredictorState state, UtilizationHistory history);
}
