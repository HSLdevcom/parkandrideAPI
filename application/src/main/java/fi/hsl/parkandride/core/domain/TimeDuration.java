// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import java.util.Comparator;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.wordnik.swagger.annotations.ApiModelProperty;

import fi.hsl.parkandride.core.domain.validation.ValidTimeDuration;

@ValidTimeDuration
public class TimeDuration implements Comparable<TimeDuration> {

    public static Comparator<TimeDuration> COMPARATOR =
            comparing(TimeDuration::getFrom, nullsLast(naturalOrder()))
                    .thenComparing(TimeDuration::getUntil, nullsLast(naturalOrder()));

    @ApiModelProperty(required = true, value="Time of day as hh[:mm] - hour (00-24) and optional minute (00-59)")
    @NotNull
    public Time from;

    @ApiModelProperty(required = true, value="Time of day as hh[:mm] - hour (00-24) and optional minute (00-59)")
    @NotNull
    public Time until;

    public TimeDuration() {}

    public TimeDuration(String from, String until) {
        this(new Time(from), new Time(until));
    }

    public TimeDuration(Time from, Time until) {
        this.from = checkNotNull(from);
        this.until = checkNotNull(until);
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

    public Time getFrom() {
        return from;
    }

    public Time getUntil() {
        return until;
    }

    public static TimeDuration add(TimeDuration td1, TimeDuration td2) {
        Time from = td1.from.isBefore(td2.from) ? td1.from : td2.from;
        Time until = td1.until.isAfter(td2.until) ? td1.until : td2.until;
        return new TimeDuration(from, until);
    }

}
