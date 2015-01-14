package fi.hsl.parkandride.core.service;

import static org.assertj.core.api.Assertions.tuple;

import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import fi.hsl.parkandride.core.domain.CapacityType;
import fi.hsl.parkandride.core.domain.DayType;
import fi.hsl.parkandride.core.domain.Pricing;
import fi.hsl.parkandride.core.domain.Usage;


public class PricingValidatorTest {
    private static final Usage[] usages = new Usage[] {  Usage.PARK_AND_RIDE, Usage.COMMERCIAL };
    private static final CapacityType[] capacities = new CapacityType[] {  CapacityType.CAR, CapacityType.BICYCLE };
    private static final DayType[] days = new DayType[] {  DayType.BUSINESS_DAY, DayType.SATURDAY };

    private static final int DEFAULT_CAPACITY = 42;

    @Test
    public void hours_can_overlap_for_different_usages() {
        Pricing a = pricing(usages[0], capacities[0], days[0], 7, 17);
        Pricing b = pricing(usages[1], capacities[0], days[0], 8, 18);

        PricingValidator.validate(maxCapacities(a, b), ImmutableSet.of(a, b));
    }

    @Test
    public void hours_can_overlap_for_different_capacities() {
        Pricing a = pricing(usages[0], capacities[0], days[0], 7, 17);
        Pricing b = pricing(usages[0], capacities[1], days[0], 8, 18);

        PricingValidator.validate(maxCapacities(a, b), ImmutableSet.of(a, b));
    }

    @Test
    public void hours_can_overlap_for_different_days() {
        Pricing a = pricing(usages[0], capacities[0], days[0], 7, 17);
        Pricing b = pricing(usages[0], capacities[0], days[1], 8, 18);

        PricingValidator.validate(maxCapacities(a, b), ImmutableSet.of(a, b));
    }

    @Test(expected = ValidationException.class)
    public void hours_cannot_overlap_for_same_usage_capacity_day() {
        Pricing a = pricing(usages[0], capacities[0], days[0], 7, 17);
        Pricing b = pricing(usages[0], capacities[0], days[0], 8, 18);

        PricingValidator.validate(maxCapacities(a, b), ImmutableSet.of(a, b));
    }

    @Test
    public void consecutive_intervals_for_same_usage_capacity_day() {
        Pricing a = pricing(usages[0], capacities[0], days[0], 8, 9);
        Pricing b = pricing(usages[0], capacities[0], days[0], 9, 10);

        PricingValidator.validate(maxCapacities(a, b), ImmutableSet.of(a, b));
    }

    @Test(expected = ValidationException.class)
    public void built_capacity_must_exist_for_pricing_capacity() {
        Pricing a = pricing(usages[0], capacities[0], days[0], 7, 17, 2);

        Map<CapacityType, Integer> builtCapacity = Maps.newHashMap();

        PricingValidator.validate(builtCapacity, ImmutableSet.of(a));
    }

    @Test(expected = ValidationException.class)
    public void single_pricing_capacity_cannot_be_larger_than_build_capacity() {
        Pricing a = pricing(usages[0], capacities[0], days[0], 7, 17, 2);

        Map<CapacityType, Integer> builtCapacity = Maps.newHashMap();
        builtCapacity.put(capacities[0], 1);

        PricingValidator.validate(builtCapacity, ImmutableSet.of(a));
    }

    @Test
    public void all_violations_are_reported() {
        Pricing a = pricing(usages[0], capacities[0], days[0], 7, 17);
        Pricing b = pricing(usages[0], capacities[0], days[0], 8, 18);
        Pricing c = pricing(usages[1], capacities[0], days[0], 7, 17);
        Pricing d = pricing(usages[1], capacities[0], days[0], 8, 18);
        Pricing e = pricing(usages[1], CapacityType.MOTORCYCLE, days[0], 8, 18);

        try {
            Map<CapacityType, Integer> builtCapacity = Maps.newHashMap();
            builtCapacity.put(a.capacityType, a.maxCapacity-1);
            builtCapacity.put(c.capacityType, c.maxCapacity-1);

            PricingValidator.validate(builtCapacity, ImmutableSet.of(a, b, c, d, e));
            Assert.fail();
        } catch (Exception ex) {
            ValidationException expected = (ValidationException) ex;

            String pricingOverlapMessage = "hour intervals cannot overlap when usage, capacity type and day type are the same";
            String capacityOverflowMessage = "pricing capacity cannot exceed built capacity";
            Assertions.assertThat(expected.violations).extracting("type", "path", "message").contains(
                    tuple("PricingOverlap", "pricing.PARK_AND_RIDE.CAR.BUSINESS_DAY", pricingOverlapMessage),
                    tuple("PricingOverlap", "pricing.COMMERCIAL.CAR.BUSINESS_DAY", pricingOverlapMessage),
                    tuple("CapacityOverflow", "pricing.PARK_AND_RIDE.CAR.BUSINESS_DAY", capacityOverflowMessage),
                    tuple("CapacityOverflow", "pricing.COMMERCIAL.CAR.BUSINESS_DAY", capacityOverflowMessage),
                    tuple("BuiltCapacityNotFound", "pricing.COMMERCIAL.MOTORCYCLE.BUSINESS_DAY", "no corresponding built capacity found for pricing capacity")
            );
        }
    }

    private static Map<CapacityType, Integer> maxCapacities(Pricing... pricing) {
        Map<CapacityType, Integer> result = Maps.newHashMap();
        for (Pricing p : pricing) {
            Integer max = result.get(p.capacityType);
            if (max == null || p.maxCapacity > max) {
                result.put(p.capacityType, p.maxCapacity);
            }
        }
        return result;
    }

    private static Pricing pricing(Usage usage, CapacityType capacity, DayType day, int from, int until) {
        return pricing(usage, capacity, day, from, until, DEFAULT_CAPACITY);
    }

    private static Pricing pricing(Usage usage, CapacityType capacity, DayType day, int from, int until, int maxCapacity) {
        return new Pricing(usage, capacity, maxCapacity, day, String.valueOf(from), String.valueOf(until), "42");
    }
}