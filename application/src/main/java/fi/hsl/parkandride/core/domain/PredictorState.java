// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import org.joda.time.DateTime;

public class PredictorState {
    // TODO: same as PredictionBatch.sourceTimestamp? rename to latestUtilization (but nupic needs other state as well)
    public DateTime latestProcessed = new DateTime(0);
    public String internalState = "";
}
