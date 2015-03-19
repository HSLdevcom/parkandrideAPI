// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class TimeTest {

    @Test
    public void beginning_of_day() {
        Time t = new Time(0);
        assertThat(t.toString()).isEqualTo("00");
        assertThat(t).isEqualTo(new Time("00"));
        assertThat(t.hashCode()).isEqualTo(0);
        assertThat(t.getHour()).isEqualTo(0);
        assertThat(t.getMinute()).isEqualTo(0);
    }

    @Test
    public void end_of_day() {
        Time t = new Time(24*60);
        assertThat(t.toString()).isEqualTo("24");
        assertThat(t).isEqualTo(new Time("24"));
        assertThat(t.hashCode()).isEqualTo(24*60);
        assertThat(t.getHour()).isEqualTo(24);
        assertThat(t.getMinute()).isEqualTo(0);
    }

    @Test
    public void abbreviated_hour() {
        assertThat(new Time("9").toString()).isEqualTo("09");
    }

    @Test
    public void early_hour_and_minutes_to_string() {
        assertThat(new Time("09:09").toString()).isEqualTo("09:09");
    }

    @Test
    public void later_hour_and_minutes_to_string() {
        assertThat(new Time("20:30").toString()).isEqualTo("20:30");
    }

    @Test
    public void afternoon() {
        assertThat(new Time("16").getMinuteOfDay()).isEqualTo(16*60);
    }

    @Test
    public void beginning_of_day_is_before_end_of_day() {
        assertThat(new Time("00").compareTo(new Time("24"))).isLessThan(0);
    }

    @Test
    public void parse_minutes() {
        assertThat(new Time("23:59").getMinuteOfDay()).isEqualTo(60*23 + 59);
    }

    @Test(expected = IllegalArgumentException.class)
    public void one_digit_minute() {
        new Time("1:1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegal_hour() {
        new Time("25");
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegal_minute() {
        new Time("9:60");
    }

    @Test(expected = IllegalArgumentException.class)
    public void negative_hour() {
        new Time("-1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void negative_minute() {
        new Time("00:-1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegal_next_day() {
        new Time("24:01");
    }

}
