// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import java.util.Comparator;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class Pricing {

    public static Comparator<Pricing> COMPARATOR =
            comparing(Pricing::getCapacityType, nullsLast(naturalOrder()))
            .thenComparing(Pricing::getUsage, nullsLast(naturalOrder()))
            .thenComparing(Pricing::getDayType, nullsLast(naturalOrder()))
            .thenComparing(Pricing::getTime, nullsLast(naturalOrder()));

    @NotNull
    public Usage usage;

    @NotNull
    public CapacityType capacityType;

    @NotNull
    @Min(1)
    public int maxCapacity;


    @NotNull
    @Valid
    public DayType dayType;

    @NotNull
    @Valid
    public TimeDuration time = new TimeDuration();


    @Valid
    public MultilingualString price;

    public Pricing() {}

    public Pricing(CapacityType capacityType, Usage usage, int maxCapacity, DayType dayType, String from, String until, String price) {
        this.usage = usage;
        this.capacityType = capacityType;
        this.maxCapacity = maxCapacity;
        this.dayType = dayType;
        this.time = new TimeDuration(new Time(from), new Time(until));
        this.price = (price != null ? new MultilingualString(price) : null);
    }

    public boolean overlaps(Pricing that) {
        if (usage == that.usage && capacityType == that.capacityType && dayType == that.dayType) {
            return this.time.overlaps(that.time);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = (capacityType == null ? 0 : capacityType.hashCode());
        hashCode = 31*hashCode + (usage == null ? 0 : usage.hashCode());
        hashCode = 31*hashCode + (dayType == null ? 0 : dayType.hashCode());
        hashCode = 31*hashCode + (time == null ? 0 : time.hashCode());
        hashCode = 31*hashCode + maxCapacity;
        hashCode = 31*hashCode + (price == null ? 0 : price.hashCode());
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Pricing) {
            Pricing other = (Pricing) obj;
            return Objects.equals(this.capacityType, other.capacityType) &&
                    Objects.equals(this.usage, other.usage) &&
                    Objects.equals(this.dayType, other.dayType) &&
                    Objects.equals(this.time, other.time) &&
                    Objects.equals(this.maxCapacity, other.maxCapacity) &&
                    Objects.equals(this.price, other.price);
        } else {
            return false;
        }
    }

    // NOTE: getters'n'setters are required for COMPARATOR

    public TimeDuration getTime() {
        return time;
    }

    public DayType getDayType() {
        return dayType;
    }

    public CapacityType getCapacityType() {
        return capacityType;
    }

    public Usage getUsage() {
        return usage;
    }
}
