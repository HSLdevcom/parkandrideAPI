package fi.hsl.parkandride.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.hsl.parkandride.core.domain.*;

public final class PricingValidator {
    private PricingValidator() {}

    public static void validate(Map<CapacityType, Integer> builtCapacity, Set<Pricing> pricing) {
        List<Violation> violations = new ArrayList<>();

        validateTotalCapacity(builtCapacity, pricing, violations);
        validateHourOverlap(pricing.toArray(new Pricing[pricing.size()]), violations);

        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }
    }

    private static void validateTotalCapacity(Map<CapacityType, Integer> builtCapacityByType, Set<Pricing> pricing, List<Violation> violations) {
        pricing.stream().forEach((p) -> {
            Integer buildCapacity = builtCapacityByType.get(p.capacityType);
            if (buildCapacity == null) {
                violations.add(new Violation("BuiltCapacityNotFound", buildViolationType(p), "no corresponding built capacity found for pricing capacity"));
                return;
            }

            if (buildCapacity < p.maxCapacity) {
                violations.add(new Violation("CapacityOverflow", buildViolationType(p), "pricing capacity cannot exceed built capacity"));
            }
        });
    }

    private static void validateHourOverlap(Pricing[] pricing, List<Violation> violations) {
        int len = pricing.length;
        if (len > 1) {
            for (int i = 0; i + 1 < len; ++i) {
                for (int j = i + 1; j < len; ++j) {
                    if (pricing[i].overlaps(pricing[j])) {
                        violations.add(new Violation("PricingOverlap", buildViolationType(pricing[i]), "hour intervals cannot overlap when usage, " +
                                "capacity type and day type are the same"));
                    }
                }
            }
        }
    }

    private static String buildViolationType(Pricing p) {
        return "pricing." + p.usage + "." + p.capacityType + "." + p.dayType;
    }
}
