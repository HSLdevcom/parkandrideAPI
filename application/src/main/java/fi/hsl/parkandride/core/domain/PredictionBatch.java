// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;


import org.joda.time.DateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class PredictionBatch {

    @NotNull public Long facilityId;
    @NotNull public CapacityType capacityType;
    @NotNull public Usage usage;

    @NotNull public DateTime sourceTimestamp;
    @NotNull @Valid public List<Prediction> predictions = new ArrayList<>();
}
