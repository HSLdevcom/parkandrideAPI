package fi.hsl.parkandride;

import static fi.hsl.parkandride.MDCFilter.validateAppId;

import org.junit.Test;

import fi.hsl.parkandride.front.IllegalHeaderException;

public class MDCFilterTest {

    @Test
    public void liipi_ui_is_valid() {
        validateAppId("liipi-ui");
    }

    @Test(expected = IllegalHeaderException.class)
    public void too_short_appid() {
        validateAppId("li");
    }

    @Test(expected = IllegalHeaderException.class)
    public void too_long_appid() {
        validateAppId("123456789_123456789_x");
    }

    @Test
    public void valid_character_classes() {
        validateAppId("a-zA-Z_0.1/9");
    }

}