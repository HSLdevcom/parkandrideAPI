package fi.hsl.parkandride.core.domain;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import java.util.Comparator;
import java.util.Objects;

import javax.validation.constraints.NotNull;

@ValidTimeDuration
public class TimeDuration implements Comparable<TimeDuration> {

    public static Comparator<TimeDuration> COMPARATOR =
            comparing(((TimeDuration d) -> d.from), nullsLast(naturalOrder()))
                    .thenComparing(((TimeDuration d) -> d.until), nullsLast(naturalOrder()));

    @NotNull
    public Time from;

    @NotNull
    public Time until;

    public TimeDuration() {}

    public TimeDuration(Time from, Time until) {
        this.from = from;
        this.until = until;
    }

    public boolean overlaps(TimeDuration that) {
        return from.getMinuteOfDay() < that.until.getMinuteOfDay() && that.from.getMinuteOfDay() < until.getMinuteOfDay();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof TimeDuration) {
            TimeDuration other = (TimeDuration) obj;
            return Objects.equals(this.from, other.from) &&
                    Objects.equals(this.until, other.until);
        } else {
            return false;
        }
    }

    public int hashCode() {
        int hashCode = (from == null ? 0 : from.hashCode());
        return 31*hashCode + (until == null ? 0 : until.hashCode());
    }

    @Override
    public int compareTo(TimeDuration o) {
        return COMPARATOR.compare(this, o);
    }
}
