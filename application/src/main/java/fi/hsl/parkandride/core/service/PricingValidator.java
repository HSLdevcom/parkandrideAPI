package fi.hsl.parkandride.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.hsl.parkandride.core.domain.*;

public final class PricingValidator {
    private PricingValidator() {}

    public static void validate(Map<CapacityType, Integer> builtCapacity, List<Pricing> pricing) {
        List<Violation> violations = new ArrayList<>();
        validate(builtCapacity, pricing, violations);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }
    }

    public static void validate(Map<CapacityType, Integer> builtCapacity, List<Pricing> pricing, Collection<Violation> violations) {
        validateTotalCapacity(builtCapacity, pricing, violations);
        validateHourOverlap(pricing, violations);
    }

    private static void validateTotalCapacity(Map<CapacityType, Integer> builtCapacityByType, List<Pricing> pricing, Collection<Violation> violations) {
        for (int i=0; i < pricing.size(); i++) {
            Pricing p = pricing.get(i);
            Integer buildCapacity = builtCapacityByType.get(p.capacityType);
            if (buildCapacity == null) {
                violations.add(new Violation("BuiltCapacityNotFound", buildViolationType(i, "capacityType"), "no corresponding built capacity found for " +
                        "pricing capacity"));
                return;
            }

            if (buildCapacity < p.maxCapacity) {
                violations.add(new Violation("CapacityOverflow", buildViolationType(i, "maxCapacity"), "pricing capacity cannot exceed built capacity"));
            }
        };
    }

    private static void validateHourOverlap(List<Pricing> pricing, Collection<Violation> violations) {
        int len = pricing.size();
        if (len > 1) {
            for (int i = 0; i + 1 < len; ++i) {
                for (int j = i + 1; j < len; ++j) {
                    if (pricing.get(i).overlaps(pricing.get(j))) {
                        violations.add(new Violation("PricingOverlap", buildViolationType(j, "time"), "hour intervals cannot overlap when usage, " +
                                "capacity type and day type are the same"));
                    }
                }
            }
        }
    }

    private static String buildViolationType(int index, String property) {
        return "pricing[" + index + "]." + property;
    }

}
