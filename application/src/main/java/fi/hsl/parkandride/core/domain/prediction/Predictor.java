// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.prediction;

import java.util.List;

public interface Predictor {

    String getType();

    List<Prediction> predict(PredictorState state, UtilizationHistory history);
}
