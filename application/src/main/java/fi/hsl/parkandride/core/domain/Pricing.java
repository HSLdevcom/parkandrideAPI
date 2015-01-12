package fi.hsl.parkandride.core.domain;

import static fi.hsl.parkandride.core.domain.CompareUtil.comparingNullsLast;
import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;

import java.util.Comparator;
import java.util.Objects;

import javax.validation.constraints.NotNull;

public class Pricing implements Comparable<Pricing> {

    public static Comparator<Pricing> COMPARATOR =
            comparingNullsLast((Pricing p) -> p.capacityType)
            .thenComparing(comparingNullsLast((Pricing p) -> p.usage))
            .thenComparing(comparingNullsLast((Pricing p) -> p.dayType))
            .thenComparing(comparingNullsLast((Pricing p) -> p.from))
            .thenComparing(comparingNullsLast((Pricing p) -> p.until))
            .thenComparing(comparing((Pricing p) -> p.maxCapacity))
            .thenComparing(nullsLast((Pricing p1, Pricing p2) -> MultilingualString.COMPARATOR.compare(p1.price, p2.price)));

    @NotNull
    public Usage usage;

    @NotNull
    public CapacityType capacityType;

    @NotNull
    public int maxCapacity;


    @NotNull
    public DayType dayType;

    @NotNull
    public Time from;

    @NotNull
    public Time until;


    public MultilingualString price;

    public Pricing() {}

    public Pricing(Usage usage, CapacityType capacityType, int maxCapacity, DayType dayType, String from, String until, String price) {
        this.usage = usage;
        this.capacityType = capacityType;
        this.maxCapacity = maxCapacity;
        this.dayType = dayType;
        this.from = new Time(from);
        this.until = new Time(until);
        this.price = (price != null ? new MultilingualString(price) : null);
    }

    @Override
    public int compareTo(Pricing other) {
        return COMPARATOR.compare(this, other);
    }

    public int hashCode() {
        int hashCode = (capacityType == null ? 0 : capacityType.hashCode());
        hashCode = 31*hashCode + (usage == null ? 0 : usage.hashCode());
        hashCode = 31*hashCode + (dayType == null ? 0 : dayType.hashCode());
        hashCode = 31*hashCode + (from == null ? 0 : from.hashCode());
        hashCode = 31*hashCode + (until == null ? 0 : until.hashCode());
        hashCode = 31*hashCode + maxCapacity;
        hashCode = 31*hashCode + (price == null ? 0 : price.hashCode());
        return hashCode;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Pricing) {
            Pricing other = (Pricing) obj;
            return Objects.equals(this.capacityType, other.capacityType) &&
                    Objects.equals(this.usage, other.usage) &&
                    Objects.equals(this.dayType, other.dayType) &&
                    Objects.equals(this.from, other.from) &&
                    Objects.equals(this.until, other.until) &&
                    Objects.equals(this.maxCapacity, other.maxCapacity) &&
                    Objects.equals(this.price, other.price);
        } else {
            return false;
        }
    }

}
