package fi.hsl.parkandride.core.domain;

import static fi.hsl.parkandride.core.domain.Usage.PARK_AND_RIDE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum PricingMethod {
    PARK_AND_RIDE_247_FREE {
        @Override
        public List<Pricing> getPricing(Facility facility) {
            return free24h(facility.builtCapacity, PARK_AND_RIDE);
        }
    },
    CUSTOM {
        @Override
        public List<Pricing> getPricing(Facility facility) {
            return facility.pricing;
        }
    };

    public abstract List<Pricing> getPricing(Facility facility);

    private static List<Pricing> free24h(Map<CapacityType, Integer> builtCapacity, Usage usage) {
        final List<Pricing> pricing = new ArrayList<>();
        builtCapacity.entrySet().forEach( e -> {
            for (DayType dayType : DayType.values()) {
                pricing.add(free24h(e.getKey(), usage, e.getValue(), dayType));
            }
        });
        return pricing;
    }

    private static Pricing free24h(CapacityType capacityType, Usage usage, int maxCapacity, DayType dayType) {
        return new Pricing(capacityType, usage, maxCapacity, dayType, "00", "24", null);
    }

}
