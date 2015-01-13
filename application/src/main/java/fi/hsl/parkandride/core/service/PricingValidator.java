package fi.hsl.parkandride.core.service;

import java.util.Set;

import fi.hsl.parkandride.core.domain.*;

public class PricingValidator {
    public void validate(Set<Pricing> pricing) {
        validateHourOverlap(pricing.toArray(new Pricing[pricing.size()]));
    }

    private void validateHourOverlap(Pricing[] pricing) {
        int len = pricing.length;
        if (len > 1) {
            for (int i = 0; i + 1 < len; ++i) {
                for (int j = i + 1; j < len; ++j) {
                    if (pricing[i].overlaps(pricing[j])) {
                        throw new ValidationException(new Violation("not jepa"));
                    }
                }
            }
        }
    }
}
