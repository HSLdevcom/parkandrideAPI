package fi.hsl.parkandride.core.domain;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.Integer.compare;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Time extends Number implements Comparable<Time> {

    public static final int MAX = 24 * 60;  // 1440

    public static final Pattern PATTERN = Pattern.compile("(\\d?\\d)(?::(\\d\\d))?");

    private final int time;

    public Time(int minutes) {
        if (minutes < 0 || minutes > MAX) {
            throw new IllegalArgumentException("Expected time between 00:00 (0) and 24:00 (1440)");
        }
        this.time = minutes;
    }

    public Time(String time) {
        this(parseTime(time));
    }

    public static int parseTime(String time) {
        if (isNullOrEmpty(time)) {
            throw new IllegalArgumentException("time should not be null or empty");
        }
        Matcher m = PATTERN.matcher(time);
        if (m.matches()) {
            int hour = Integer.parseInt(m.group(1));
            int minute = 0;
            String optionalMinute = m.group(2);
            if (optionalMinute != null) {
                minute = Integer.parseInt(optionalMinute);
            }

            if (hour > 24) {
                throw new IllegalArgumentException("hour should be <= 24");
            }
            if (minute > 59) {
                throw new IllegalArgumentException("minute should be <= 59");
            }
            return 60 * hour + minute;
        } else {
            throw new IllegalArgumentException("expected time in 24-h format, e.g.  9, 09, 24, 9:59, 23:01");
        }
    }

    public int getHour() {
        return time / 60;
    }

    public int getMinute() {
        return time % 60;
    }

    public int getMinuteOfDay() {
        return time;
    }

    public int hashCode() {
        return time;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Time) {
            Time other = (Time) obj;
            return this.time == other.time;
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(Time other) {
        return compare(this.time, other.time);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(5);
        append(getHour(), sb);
        int minute = getMinute();
        if (minute > 0) {
            sb.append(':');
            append(getMinute(), sb);
        }
        return sb.toString();
    }

    private void append(int val, StringBuilder sb) {
        if (val < 10) {
            sb.append('0');
        }
        sb.append(val);
    }

    public boolean isBefore(Time other) {
        return this.time < other.time;
    }

    @Override
    public int intValue() {
        return time;
    }

    @Override
    public long longValue() {
        return time;
    }

    @Override
    public float floatValue() {
        return time;
    }

    @Override
    public double doubleValue() {
        return time;
    }
}
