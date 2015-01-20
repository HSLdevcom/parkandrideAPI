package fi.hsl.parkandride.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mysema.commons.lang.Pair;

import fi.hsl.parkandride.core.domain.CapacityType;
import fi.hsl.parkandride.core.domain.Pricing;
import fi.hsl.parkandride.core.domain.UnavailableCapacity;
import fi.hsl.parkandride.core.domain.Usage;
import fi.hsl.parkandride.core.domain.Violation;

public final class CapacityPricingValidator {
    private CapacityPricingValidator() {}

    public static void validate(Map<CapacityType, Integer> builtCapacity,
                                List<Pricing> pricing,
                                List<UnavailableCapacity> unavailableCapacities,
                                Collection<Violation> violations) {
        Map<Pair<CapacityType, Usage>, Integer> typeUsageMax = Maps.newHashMap();
        for (int i=0; i < pricing.size(); i++) {
            Pricing p = pricing.get(i);
            registerTypeUsageMaxCapacity(p, typeUsageMax);
            validateTotalCapacity(builtCapacity.get(p.capacityType), p, i, violations);
            validateHourOverlap(pricing, i, violations);
        }
        validateUnavailableCapacities(unavailableCapacities, typeUsageMax, violations);
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
            if (pricing.get(i).overlaps(pricing.get(j))) {
                violations.add(pricingOverlap(j));
            }
        }
    }

    private static void validateUnavailableCapacities(List<UnavailableCapacity> unavailableCapacities,
                                                      Map<Pair<CapacityType, Usage>, Integer> typeUsageMax,
                                                      Collection<Violation> violations) {
        Set<Pair<CapacityType, Usage>> uniqueTypeUsage = Sets.newHashSet();
        for (int i=0; i < unavailableCapacities.size(); i++) {
            UnavailableCapacity uc = unavailableCapacities.get(i);
            Pair<CapacityType, Usage> typeUsage = new Pair(uc.capacityType, uc.usage);
            if (uniqueTypeUsage.add(typeUsage)) {
                Integer max = typeUsageMax.get(typeUsage);
                if (max == null) {
                    violations.add(pricingNotFound(i));
                } else if (uc.capacity > max.intValue()) {
                    violations.add(unavailableCapacityOverflow(i));
                }
            } else {
                violations.add(duplicateUnavailableCapacity(i));
            }
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
