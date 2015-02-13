package fi.hsl.parkandride.core.service;

import static fi.hsl.parkandride.core.domain.PricingMethod.CUSTOM;
import static fi.hsl.parkandride.core.domain.PricingMethod.HSL_247_FREE;
import static fi.hsl.parkandride.core.domain.PricingMethod.PARK_AND_RIDE_247_FREE;
import static fi.hsl.parkandride.core.domain.Usage.HSL;
import static fi.hsl.parkandride.core.domain.Usage.PARK_AND_RIDE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mysema.commons.lang.Pair;

import fi.hsl.parkandride.core.domain.*;

public final class CapacityPricingValidator {
    private CapacityPricingValidator() {}

    public static void validateAndNormalize(Facility facility, Collection<Violation> violations) {
        if (facility.pricingMethod == CUSTOM) {
            validateAndNormalizeCustomPricing(facility.builtCapacity, facility.pricing, facility.unavailableCapacities, violations);
        }
        else if (facility.pricingMethod == PARK_AND_RIDE_247_FREE) {
            validateAndNormalizeUnavailableCapacities(facility.unavailableCapacities, typeUsageMaxForUsage(facility.builtCapacity, PARK_AND_RIDE), violations);
        }
        else if (facility.pricingMethod == HSL_247_FREE) {
            validateAndNormalizeUnavailableCapacities(facility.unavailableCapacities, typeUsageMaxForUsage(facility.builtCapacity, HSL), violations);
        }
    }

    public static void validateAndNormalizeCustomPricing(Map<CapacityType, Integer> builtCapacity,
                                                         List<Pricing> pricing,
                                                         List<UnavailableCapacity> unavailableCapacities,
                                                         Collection<Violation> violations) {
        if (builtCapacity == null || pricing == null || unavailableCapacities == null) {
            return;
        }
        Map<Pair<CapacityType, Usage>, Integer> typeUsageMax = Maps.newHashMap();
        for (int i=0; i < pricing.size(); i++) {
            Pricing p = pricing.get(i);
            if (p != null) {
                registerTypeUsageMaxCapacity(p, typeUsageMax);
                validateTotalCapacity(builtCapacity.get(p.capacityType), p, i, violations);
                validateHourOverlap(pricing, i, violations);
            }
        }
        validateAndNormalizeUnavailableCapacities(unavailableCapacities, typeUsageMax, violations);
    }

    private static Map<Pair<CapacityType, Usage>, Integer> typeUsageMaxForUsage(final Map<CapacityType, Integer> builtCapacity, final Usage usage) {
        return builtCapacity.entrySet().stream().collect(Collectors.toMap(
                (Map.Entry<CapacityType, Integer> e) -> new Pair<>(e.getKey(), usage),
                (Map.Entry<CapacityType, Integer> e) -> e.getValue()
        ));
    }

    private static void registerTypeUsageMaxCapacity(Pricing p, Map<Pair<CapacityType, Usage>, Integer> typeUsageMax) {
        Pair<CapacityType, Usage> typeUsage = new Pair(p.capacityType, p.usage);
        Integer prevMax = typeUsageMax.get(typeUsage);
        if (prevMax == null || prevMax.intValue() < p.maxCapacity) {
            typeUsageMax.put(typeUsage, p.maxCapacity);
        }
    }

    private static void validateTotalCapacity(Integer builtCapacity, Pricing p, int i, Collection<Violation> violations) {
        if (builtCapacity == null) {
            violations.add(builtCapacityNotFound(i));
        }
        else if (builtCapacity < p.maxCapacity) {
            violations.add(pricingCapacityOverflow(i));
        }
    }

    private static void validateHourOverlap(List<Pricing> pricing, int i, Collection<Violation> violations) {
        for (int j = i + 1; j < pricing.size(); ++j) {
            Pricing pi = pricing.get(i);
            Pricing pj = pricing.get(j);
            if (pi != null && pj != null && pi.overlaps(pj)) {
                violations.add(pricingOverlap(j));
            }
        }
    }

    private static void validateAndNormalizeUnavailableCapacities(List<UnavailableCapacity> unavailableCapacities,
                                                                  Map<Pair<CapacityType, Usage>, Integer> typeUsageMax,
                                                                  Collection<Violation> violations) {
        if (unavailableCapacities == null) {
            return;
        }
        List<UnavailableCapacity> normalized = new ArrayList<>(unavailableCapacities.size());
        Set<Pair<CapacityType, Usage>> uniqueTypeUsage = Sets.newHashSet();
        for (int i=0; i < unavailableCapacities.size(); i++) {
            UnavailableCapacity uc = unavailableCapacities.get(i);
            if (uc != null) {
                Pair<CapacityType, Usage> typeUsage = new Pair(uc.capacityType, uc.usage);
                if (uniqueTypeUsage.add(typeUsage)) {
                    Integer max = typeUsageMax.get(typeUsage);
                    if (max == null) {
                        continue;
                    } else if (uc.capacity > max.intValue()) {
                        violations.add(unavailableCapacityOverflow(i));
                    }
                } else {
                    violations.add(duplicateUnavailableCapacity(i));
                }
                normalized.add(uc);
            }
        }
        if (unavailableCapacities.size() != normalized.size()) {
            unavailableCapacities.clear();
            unavailableCapacities.addAll(normalized);
        }
    }

    private static Violation duplicateUnavailableCapacity(int i) {
        return new Violation("DuplicateUnavailableCapacity", "unavailableCapacities[" + i + "]",
                "duplicate capacityType and usage");
    }

    private static Violation unavailableCapacityOverflow(int i) {
        return new Violation("UnavailableCapacityOverflow", "unavailableCapacities[" + i + "].capacity",
                "unavailable capacity cannot exceed max capacity of pricing");
    }

    private static Violation pricingNotFound(int i) {
        return new Violation("PricingNotFound", "unavailableCapacities[" + i + "]",
                "no corresponding pricing for unavailable capacity");
    }

    private static Violation pricingCapacityOverflow(int i) {
        return new Violation("PricingCapacityOverflow", "pricing[" + i + "].maxCapacity",
                "pricing capacity cannot exceed built capacity");
    }

    private static Violation builtCapacityNotFound(int i) {
        return new Violation("BuiltCapacityNotFound", "pricing[" + i + "].capacityType",
                "no corresponding built capacity found for pricing capacity");
    }

    private static Violation pricingOverlap(int j) {
        return new Violation("PricingOverlap", "pricing[" + j + "].time",
                "hour intervals cannot overlap when usage, capacity type and day type are the same");
    }

}
