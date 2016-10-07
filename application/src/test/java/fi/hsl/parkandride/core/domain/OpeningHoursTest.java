// Copyright © 2016 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static fi.hsl.parkandride.core.domain.CapacityType.*;
import static fi.hsl.parkandride.core.domain.DayType.BUSINESS_DAY;
import static fi.hsl.parkandride.core.domain.Usage.COMMERCIAL;
import static fi.hsl.parkandride.core.domain.Usage.PARK_AND_RIDE;
import static org.assertj.core.api.Assertions.assertThat;

public class OpeningHoursTest {

    private final OpeningHours openingHours = new OpeningHours();

    @Test
    public void empty_opening_hours() {
        List<Pricing> pricing = new ArrayList<>();

        openingHours.initialize(pricing);

        assertThat(openingHours.byDayType).isEqualTo(ImmutableMap.of());
    }

    @Test
    public void opening_hours_is_min_max_over_capacity_types_and_usages() {
        // BUSINESS_DAY: 8-22
        List<Pricing> pricing = new ArrayList<>();
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, BUSINESS_DAY, "17", "22", null));
        pricing.add(new Pricing(ELECTRIC_CAR, PARK_AND_RIDE, 10, BUSINESS_DAY, "12", "19", null));
        pricing.add(new Pricing(BICYCLE, COMMERCIAL, 10, BUSINESS_DAY, "8", "10", null));

        openingHours.initialize(pricing);

        assertThat(openingHours.byDayType).isEqualTo(ImmutableMap.of(
                BUSINESS_DAY, new TimeDuration("8", "22")
        ));
    }
}
