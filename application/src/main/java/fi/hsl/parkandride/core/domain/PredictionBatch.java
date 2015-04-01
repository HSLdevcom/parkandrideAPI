// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;


import org.joda.time.DateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class PredictionBatch {

    @NotNull @Valid public UtilizationKey utilizationKey = new UtilizationKey();
    @NotNull public DateTime sourceTimestamp;
    @NotNull @Valid public List<Prediction> predictions = new ArrayList<>();
}
