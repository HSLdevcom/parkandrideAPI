// Copyright © 2016 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import com.google.common.collect.ImmutableMap;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static fi.hsl.parkandride.core.domain.CapacityType.*;
import static fi.hsl.parkandride.core.domain.DayType.*;
import static fi.hsl.parkandride.core.domain.Usage.COMMERCIAL;
import static fi.hsl.parkandride.core.domain.Usage.PARK_AND_RIDE;
import static org.assertj.core.api.Assertions.assertThat;

public class OpeningHoursTest {

    private static final LocalDate SOME_MONDAY = new LocalDate(2000, 1, 3);
    private static final LocalDate SOME_TUESDAY = new LocalDate(2000, 1, 4);
    private static final LocalDate SOME_WEDNESDAY = new LocalDate(2000, 1, 5);
    private static final LocalDate SOME_THURSDAY = new LocalDate(2000, 1, 6);
    private static final LocalDate SOME_FRIDAY = new LocalDate(2000, 1, 7);
    private static final LocalDate SOME_SATURDAY = new LocalDate(2000, 1, 8);
    private static final LocalDate SOME_SUNDAY = new LocalDate(2000, 1, 9);

    private final OpeningHours openingHours = new OpeningHours();
    private final List<Pricing> pricing = new ArrayList<>();

    @Test
    public void empty_opening_hours() {
        openingHours.initialize(pricing, new DateTime());

        assertThat(openingHours.byDayType).isEqualTo(ImmutableMap.of());
        assertThat(openingHours.openNow).isFalse();
    }

    @Test
    public void opening_hours_is_min_max_over_capacity_types_and_usages() {
        // BUSINESS_DAY: 8-22
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, BUSINESS_DAY, "17", "22", null));
        pricing.add(new Pricing(ELECTRIC_CAR, PARK_AND_RIDE, 10, BUSINESS_DAY, "12", "19", null));
        pricing.add(new Pricing(BICYCLE, COMMERCIAL, 10, BUSINESS_DAY, "8", "10", null));

        openingHours.initialize(pricing, new DateTime());

        assertThat(openingHours.byDayType).isEqualTo(ImmutableMap.of(
                BUSINESS_DAY, new TimeDuration("8", "22")
        ));
    }


    // open now: opening and closing time range checks

    @Test
    public void is_closed_before_opening_hour() {
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, BUSINESS_DAY, "10:00", "20:00", null));

        openingHours.initialize(pricing, SOME_MONDAY.toDateTime(new LocalTime(9, 59)));

        assertThat(openingHours.openNow).isFalse();
    }

    @Test
    public void is_open_on_opening_hour() {
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, BUSINESS_DAY, "10:00", "20:00", null));

        openingHours.initialize(pricing, SOME_MONDAY.toDateTime(new LocalTime(10, 0)));

        assertThat(openingHours.openNow).isTrue();
    }

    @Test
    public void is_open_between_opening_and_closing_hours() {
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, BUSINESS_DAY, "10:00", "20:00", null));

        openingHours.initialize(pricing, SOME_MONDAY.toDateTime(new LocalTime(14, 0)));

        assertThat(openingHours.openNow).isTrue();
    }

    @Test
    public void is_open_on_closing_hour() {
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, BUSINESS_DAY, "10:00", "20:00", null));

        openingHours.initialize(pricing, SOME_MONDAY.toDateTime(new LocalTime(20, 0)));

        assertThat(openingHours.openNow).isTrue();
    }

    @Test
    public void is_closed_after_closing_hour() {
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, BUSINESS_DAY, "10:00", "20:00", null));

        openingHours.initialize(pricing, SOME_MONDAY.toDateTime(new LocalTime(20, 1)));

        assertThat(openingHours.openNow).isFalse();
    }


    // open now: day of week checks

    @Test
    public void uses_business_day_schedule_on_Monday() {
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, BUSINESS_DAY, "10:00", "20:00", null));
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, SATURDAY, "12:00", "16:00", null));
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, SUNDAY, "12:00", "16:00", null));

        openingHours.initialize(pricing, SOME_MONDAY.toDateTime(new LocalTime(11, 0)));

        assertThat(openingHours.openNow).isTrue();
    }

    @Test
    public void uses_business_day_schedule_on_Tuesday() {
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, BUSINESS_DAY, "10:00", "20:00", null));
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, SATURDAY, "12:00", "16:00", null));
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, SUNDAY, "12:00", "16:00", null));

        openingHours.initialize(pricing, SOME_TUESDAY.toDateTime(new LocalTime(11, 0)));

        assertThat(openingHours.openNow).isTrue();
    }

    @Test
    public void uses_business_day_schedule_on_Wednesday() {
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, BUSINESS_DAY, "10:00", "20:00", null));
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, SATURDAY, "12:00", "16:00", null));
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, SUNDAY, "12:00", "16:00", null));

        openingHours.initialize(pricing, SOME_WEDNESDAY.toDateTime(new LocalTime(11, 0)));

        assertThat(openingHours.openNow).isTrue();
    }

    @Test
    public void uses_business_day_schedule_on_Thursday() {
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, BUSINESS_DAY, "10:00", "20:00", null));
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, SATURDAY, "12:00", "16:00", null));
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, SUNDAY, "12:00", "16:00", null));

        openingHours.initialize(pricing, SOME_THURSDAY.toDateTime(new LocalTime(11, 0)));

        assertThat(openingHours.openNow).isTrue();
    }

    @Test
    public void uses_business_day_schedule_on_Friday() {
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, BUSINESS_DAY, "10:00", "20:00", null));
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, SATURDAY, "12:00", "16:00", null));
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, SUNDAY, "12:00", "16:00", null));

        openingHours.initialize(pricing, SOME_FRIDAY.toDateTime(new LocalTime(11, 0)));

        assertThat(openingHours.openNow).isTrue();
    }

    @Test
    public void uses_Saturday_schedule_on_Saturday() {
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, BUSINESS_DAY, "12:00", "16:00", null));
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, SATURDAY, "10:00", "20:00", null));
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, SUNDAY, "12:00", "16:00", null));

        openingHours.initialize(pricing, SOME_SATURDAY.toDateTime(new LocalTime(11, 0)));

        assertThat(openingHours.openNow).isTrue();
    }

    @Test
    public void uses_Sunday_schedule_on_Sunday() {
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, BUSINESS_DAY, "12:00", "16:00", null));
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, SATURDAY, "12:00", "16:00", null));
        pricing.add(new Pricing(CAR, PARK_AND_RIDE, 10, SUNDAY, "10:00", "20:00", null));

        openingHours.initialize(pricing, SOME_SUNDAY.toDateTime(new LocalTime(11, 0)));

        assertThat(openingHours.openNow).isTrue();
    }
}
