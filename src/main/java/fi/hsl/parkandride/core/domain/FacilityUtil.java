package fi.hsl.parkandride.core.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.singleton;

public final class FacilityUtil {
    private FacilityUtil() { /** prevent instantiation */}

    public static Map<CapacityType, Set<Usage>> usagesByCapacityType(Facility facility) {
        Map<CapacityType, Set<Usage>> usagesByCapacityType = new HashMap<>();

        if (facility.pricingMethod == PricingMethod.PARK_AND_RIDE_247_FREE) {
            facility.builtCapacity.keySet().forEach(k -> usagesByCapacityType.put(k, singleton(Usage.PARK_AND_RIDE)));
        } else {
            facility.pricing.stream().forEach(pricing ->
                    usagesByCapacityType
                            .computeIfAbsent(pricing.capacityType, c -> newHashSet())
                            .add(pricing.usage)
            );
        }
        return usagesByCapacityType;
    }

    public static <T, C extends Collection<T>> BiFunction<C, C, C> combine() {
        return (left, right) -> {
            left.addAll(right);
            return left;
        };
    }
}
