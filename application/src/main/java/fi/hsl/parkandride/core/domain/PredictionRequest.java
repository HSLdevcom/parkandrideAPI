// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Pattern;
import java.util.regex.Matcher;

import static org.joda.time.Duration.standardHours;
import static org.joda.time.Duration.standardMinutes;

public class PredictionRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public DateTime at;

    @Pattern(regexp = "(\\d+:)?\\d+")
    public String after;

    public DateTime requestedTime() {
        if (at != null) {
            return at;
        }
        if (after != null) {
            return DateTime.now().plus(parseRelativeTime(after));
        }
        return DateTime.now();
    }

    private static Duration parseRelativeTime(String relativeTime) {
        Matcher matcher = java.util.regex.Pattern.compile("(?:(\\d+):)?(\\d+)").matcher(relativeTime);
        if (matcher.matches()) {
            int hours = parseOptionalInt(matcher.group(1));
            int minutes = Integer.parseInt(matcher.group(2));
            return standardHours(hours).plus(standardMinutes(minutes));
        } else {
            return Duration.ZERO;
        }
    }

    private static int parseOptionalInt(String s) {
        return s == null ? 0 : Integer.parseInt(s);
    }


    // generated getters and setters

    public DateTime getAt() {
        return at;
    }

    public void setAt(DateTime at) {
        this.at = at;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }
}
