// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import static fi.hsl.parkandride.core.domain.CapacityType.BICYCLE;
import static fi.hsl.parkandride.core.domain.CapacityType.CAR;
import static fi.hsl.parkandride.core.domain.CapacityType.ELECTRIC_CAR;
import static fi.hsl.parkandride.core.domain.DayType.BUSINESS_DAY;
import static fi.hsl.parkandride.core.domain.Usage.COMMERCIAL;
import static fi.hsl.parkandride.core.domain.Usage.PARK_AND_RIDE;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class FacilityTest {

    @Test
    public void empty_opening_hours() {
        Facility facility = new Facility();
        facility.initialize();
        assertThat(facility.openingHours.byDayType).isEqualTo(ImmutableMap.of());
    }

    @Test
    public void opening_hours_over_capacity_types_and_usages() {
        Facility facility = new Facility();
        // BUSINESS_DAY: 8-22
        facility.pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, BUSINESS_DAY, "17", "22", null));
        facility.pricing.add(new Pricing(ELECTRIC_CAR, PARK_AND_RIDE, 10, BUSINESS_DAY, "12", "19", null));
        facility.pricing.add(new Pricing(BICYCLE, COMMERCIAL, 10, BUSINESS_DAY, "8", "10", null));

        facility.initialize();
        assertThat(facility.openingHours.byDayType).isEqualTo(ImmutableMap.of(
                BUSINESS_DAY, new TimeDuration("8", "22")
        ));
    }

}
