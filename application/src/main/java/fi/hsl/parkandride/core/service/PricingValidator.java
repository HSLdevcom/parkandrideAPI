package fi.hsl.parkandride.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fi.hsl.parkandride.core.domain.*;

public final class PricingValidator {
    private PricingValidator() {}

    public static void validate(Set<Pricing> pricing) {
        List<Violation> violations = new ArrayList<>();
        validateHourOverlap(pricing.toArray(new Pricing[pricing.size()]), violations);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }
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
