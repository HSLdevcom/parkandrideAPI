package fi.hsl.parkandride.core.service;

import static org.assertj.core.api.Assertions.tuple;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import fi.hsl.parkandride.core.domain.CapacityType;
import fi.hsl.parkandride.core.domain.DayType;
import fi.hsl.parkandride.core.domain.Pricing;
import fi.hsl.parkandride.core.domain.Usage;


public class PricingValidatorTest {
    private static final Usage[] usages = new Usage[] {  Usage.PARK_AND_RIDE, Usage.COMMERCIAL };
    private static final CapacityType[] capacities = new CapacityType[] {  CapacityType.CAR, CapacityType.BICYCLE };
    private static final DayType[] days = new DayType[] {  DayType.BUSINESS_DAY, DayType.SATURDAY };

    @Test
    public void hours_can_overlap_for_different_usages() {
        Pricing a = pricing(usages[0], capacities[0], days[0], 7, 17);
        Pricing b = pricing(usages[1], capacities[0], days[0], 8, 18);

        PricingValidator.validate(ImmutableSet.of(a, b));
    }

    @Test
    public void hours_can_overlap_for_different_capacities() {
        Pricing a = pricing(usages[0], capacities[0], days[0], 7, 17);
        Pricing b = pricing(usages[0], capacities[1], days[0], 8, 18);

        PricingValidator.validate(ImmutableSet.of(a, b));
    }

    @Test
    public void hours_can_overlap_for_different_days() {
        Pricing a = pricing(usages[0], capacities[0], days[0], 7, 17);
        Pricing b = pricing(usages[0], capacities[0], days[1], 8, 18);

        PricingValidator.validate(ImmutableSet.of(a, b));
    }

    @Test(expected = ValidationException.class)
    public void hours_cannot_overlap_for_same_usage_capacity_day() {
        Pricing a = pricing(usages[0], capacities[0], days[0], 7, 17);
        Pricing b = pricing(usages[0], capacities[0], days[0], 8, 18);

        PricingValidator.validate(ImmutableSet.of(a, b));
    }

    @Test
    public void consecutive_intervals_for_same_usage_capacity_day() {
        Pricing a = pricing(usages[0], capacities[0], days[0], 8, 9);
        Pricing b = pricing(usages[0], capacities[0], days[0], 9, 10);

        PricingValidator.validate(ImmutableSet.of(a, b));
    }

    @Test
    public void all_violations_are_reported() {
        Pricing a = pricing(usages[0], capacities[0], days[0], 7, 17);
        Pricing b = pricing(usages[0], capacities[0], days[0], 8, 18);
        Pricing c = pricing(usages[1], capacities[0], days[0], 7, 17);
        Pricing d = pricing(usages[1], capacities[0], days[0], 8, 18);

        try {
            PricingValidator.validate(ImmutableSet.of(a, b, c, d));
            Assert.fail();
        } catch (Exception e) {
            ValidationException expected = (ValidationException)e;

            String expectedType = "PricingOverlap";
            String expectedMessage = "hour intervals cannot overlap when usage, capacity type and day type are the same";
            Assertions.assertThat(expected.violations).extracting("type", "path", "message").contains(
                    tuple(expectedType, "pricing.PARK_AND_RIDE.CAR.BUSINESS_DAY", expectedMessage),
                    tuple(expectedType, "pricing.COMMERCIAL.CAR.BUSINESS_DAY", expectedMessage)
            );
        }
    }

    private static Pricing pricing(Usage usage, CapacityType capacity, DayType day, int from, int until) {
        return new Pricing(usage, capacity, 42, day, String.valueOf(from), String.valueOf(until), "42");
    }
}