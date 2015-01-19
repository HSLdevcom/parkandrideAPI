package fi.hsl.parkandride.core.service;

import static fi.hsl.parkandride.core.domain.CapacityType.BICYCLE;
import static fi.hsl.parkandride.core.domain.CapacityType.CAR;
import static fi.hsl.parkandride.core.domain.CapacityType.ELECTRIC_CAR;
import static fi.hsl.parkandride.core.domain.CapacityType.MOTORCYCLE;
import static fi.hsl.parkandride.core.domain.DayType.BUSINESS_DAY;
import static fi.hsl.parkandride.core.domain.DayType.SATURDAY;
import static fi.hsl.parkandride.core.domain.Usage.COMMERCIAL;
import static fi.hsl.parkandride.core.domain.Usage.PARK_AND_RIDE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import fi.hsl.parkandride.core.domain.CapacityType;
import fi.hsl.parkandride.core.domain.DayType;
import fi.hsl.parkandride.core.domain.Pricing;
import fi.hsl.parkandride.core.domain.UnavailableCapacity;
import fi.hsl.parkandride.core.domain.Usage;


public class CapacityPricingValidatorTest {

    @Test
    public void hours_can_overlap_for_different_usages() {
        Pricing a = pricing(CAR, PARK_AND_RIDE, BUSINESS_DAY, 7, 17);
        Pricing b = pricing(CAR, COMMERCIAL, BUSINESS_DAY, 8, 18);

        CapacityPricingValidator.validate(maxCapacities(a, b), ImmutableList.of(a, b), ImmutableList.of());
    }

    @Test
    public void hours_can_overlap_for_different_capacities() {
        Pricing a = pricing(CAR, PARK_AND_RIDE, BUSINESS_DAY, 7, 17);
        Pricing b = pricing(BICYCLE, PARK_AND_RIDE, BUSINESS_DAY, 8, 18);

        CapacityPricingValidator.validate(maxCapacities(a, b), ImmutableList.of(a, b), ImmutableList.of());
    }

    @Test
    public void hours_can_overlap_for_different_days() {
        Pricing a = pricing(CAR, PARK_AND_RIDE, BUSINESS_DAY, 7, 17);
        Pricing b = pricing(CAR, PARK_AND_RIDE, SATURDAY, 8, 18);

        CapacityPricingValidator.validate(maxCapacities(a, b), ImmutableList.of(a, b), ImmutableList.of());
    }

    @Test(expected = ValidationException.class)
    public void hours_cannot_overlap_for_same_usage_capacity_day() {
        Pricing a = pricing(CAR, PARK_AND_RIDE, BUSINESS_DAY, 7, 17);
        Pricing b = pricing(CAR, PARK_AND_RIDE, BUSINESS_DAY, 8, 18);

        CapacityPricingValidator.validate(maxCapacities(a, b), ImmutableList.of(a, b), ImmutableList.of());
    }

    @Test
    public void unavailable_capacity_may_equal_max() {
        Pricing a = pricing(CAR, PARK_AND_RIDE, BUSINESS_DAY, 0, 24);
        a.maxCapacity = 5;
        Pricing b = pricing(CAR, PARK_AND_RIDE, SATURDAY, 0, 24);
        b.maxCapacity = 10;
        UnavailableCapacity uc = new UnavailableCapacity(CAR, PARK_AND_RIDE, 10);

        CapacityPricingValidator.validate(maxCapacities(a, b), ImmutableList.of(a, b), ImmutableList.of(uc));
    }

    @Test
    public void pricing_order_doesnt_count_in_unavailable_capacity_max_check() {
        Pricing a = pricing(CAR, PARK_AND_RIDE, BUSINESS_DAY, 0, 24);
        a.maxCapacity = 5;
        Pricing b = pricing(CAR, PARK_AND_RIDE, SATURDAY, 0, 24);
        b.maxCapacity = 10;
        UnavailableCapacity uc = new UnavailableCapacity(CAR, PARK_AND_RIDE, 10);

        CapacityPricingValidator.validate(maxCapacities(a, b), ImmutableList.of(b, a), ImmutableList.of(uc));
    }

    @Test(expected = ValidationException.class)
    public void pricing_not_found_with_wrong_capacity_type() {
        Pricing a = pricing(CAR, PARK_AND_RIDE, BUSINESS_DAY, 0, 24);
        a.maxCapacity = 5;
        Pricing b = pricing(CAR, PARK_AND_RIDE, SATURDAY, 0, 24);
        b.maxCapacity = 10;
        UnavailableCapacity uc = new UnavailableCapacity(BICYCLE, PARK_AND_RIDE, 10);

        CapacityPricingValidator.validate(maxCapacities(a, b), ImmutableList.of(a, b), ImmutableList.of(uc));
    }

    @Test(expected = ValidationException.class)
    public void pricing_not_found_with_wrong_usage() {
        Pricing a = pricing(CAR, PARK_AND_RIDE, BUSINESS_DAY, 0, 24);
        a.maxCapacity = 5;
        Pricing b = pricing(CAR, PARK_AND_RIDE, SATURDAY, 0, 24);
        b.maxCapacity = 10;
        UnavailableCapacity uc = new UnavailableCapacity(CAR, COMMERCIAL, 10);

        CapacityPricingValidator.validate(maxCapacities(a, b), ImmutableList.of(a, b), ImmutableList.of(uc));
    }

    @Test(expected = ValidationException.class)
    public void unavailable_capacity_overflow() {
        Pricing a = pricing(CAR, PARK_AND_RIDE, BUSINESS_DAY, 0, 24);
        a.maxCapacity = 5;
        Pricing b = pricing(CAR, PARK_AND_RIDE, SATURDAY, 0, 24);
        b.maxCapacity = 10;
        UnavailableCapacity uc = new UnavailableCapacity(CAR, PARK_AND_RIDE, 11);

        CapacityPricingValidator.validate(maxCapacities(a, b), ImmutableList.of(a, b), ImmutableList.of(uc));
    }

    @Test(expected = ValidationException.class)
    public void duplicate_unavailable_capacity() {
        Pricing a = pricing(CAR, PARK_AND_RIDE, BUSINESS_DAY, 0, 24);
        a.maxCapacity = 5;
        Pricing b = pricing(CAR, PARK_AND_RIDE, SATURDAY, 0, 24);
        b.maxCapacity = 10;
        UnavailableCapacity uc = new UnavailableCapacity(CAR, PARK_AND_RIDE, 5);

        CapacityPricingValidator.validate(maxCapacities(a, b), ImmutableList.of(a, b), ImmutableList.of(uc, uc));
    }

    @Test
    public void consecutive_intervals_for_same_usage_capacity_day() {
        Pricing a = pricing(CAR, PARK_AND_RIDE, BUSINESS_DAY, 8, 9);
        Pricing b = pricing(CAR, PARK_AND_RIDE, BUSINESS_DAY, 9, 10);

        CapacityPricingValidator.validate(maxCapacities(a, b), ImmutableList.of(a, b), ImmutableList.of());
    }

    @Test(expected = ValidationException.class)
    public void built_capacity_must_exist_for_pricing_capacity() {
        Pricing a = pricing(CAR, PARK_AND_RIDE, BUSINESS_DAY, 7, 17);

        CapacityPricingValidator.validate(Maps.newHashMap(), ImmutableList.of(a), ImmutableList.of());
    }

    @Test(expected = ValidationException.class)
    public void single_pricing_capacity_cannot_be_larger_than_build_capacity() {
        Pricing a = pricing(CAR, PARK_AND_RIDE, BUSINESS_DAY, 7, 17);

        Map<CapacityType, Integer> builtCapacity = Maps.newHashMap();
        builtCapacity.put(CAR, a.maxCapacity-1);

        CapacityPricingValidator.validate(builtCapacity, ImmutableList.of(a), ImmutableList.of());
    }

    @Test
    public void all_violations_are_reported() {
        Pricing a = pricing(CAR, PARK_AND_RIDE, BUSINESS_DAY, 7, 17);
        Pricing b = pricing(CAR, PARK_AND_RIDE, BUSINESS_DAY, 8, 18);
        Pricing c = pricing(CAR, COMMERCIAL, BUSINESS_DAY, 7, 17);
        Pricing d = pricing(CAR, COMMERCIAL, BUSINESS_DAY, 8, 18);
        Pricing e = pricing(MOTORCYCLE, COMMERCIAL, BUSINESS_DAY, 8, 18);

        UnavailableCapacity uc1 = new UnavailableCapacity(ELECTRIC_CAR, PARK_AND_RIDE, 0);
        UnavailableCapacity uc2 = new UnavailableCapacity(CAR, PARK_AND_RIDE, 43);
        UnavailableCapacity uc3 = new UnavailableCapacity(CAR, PARK_AND_RIDE, 0);

        try {
            Map<CapacityType, Integer> builtCapacity = Maps.newHashMap();
            builtCapacity.put(a.capacityType, a.maxCapacity-1);
            builtCapacity.put(c.capacityType, c.maxCapacity-1);

            CapacityPricingValidator.validate(builtCapacity,
                    ImmutableList.of(a, b, c, d, e),
                    ImmutableList.of(uc1, uc2, uc3));
            Assert.fail();
        } catch (Exception ex) {
            ValidationException expected = (ValidationException) ex;

            assertThat(expected.violations.size()).isEqualTo(10);
            assertThat(expected.violations).extracting("type", "path").contains(
                    tuple("PricingOverlap", "pricing[1].time"),
                    tuple("PricingOverlap", "pricing[3].time"),
                    tuple("PricingCapacityOverflow", "pricing[0].maxCapacity"),
                    tuple("PricingCapacityOverflow", "pricing[1].maxCapacity"),
                    tuple("PricingCapacityOverflow", "pricing[2].maxCapacity"),
                    tuple("PricingCapacityOverflow", "pricing[3].maxCapacity"),
                    tuple("BuiltCapacityNotFound", "pricing[4].capacityType"),
                    tuple("PricingNotFound", "unavailableCapacities[0]"),
                    tuple("UnavailableCapacityOverflow", "unavailableCapacities[1].capacity"),
                    tuple("DuplicateUnavailableCapacity", "unavailableCapacities[2]")
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

    private static Pricing pricing(CapacityType capacity, Usage usage, DayType day, int from, int until) {
        return new Pricing(capacity, usage, 42, day, String.valueOf(from), String.valueOf(until), "42");
    }
}