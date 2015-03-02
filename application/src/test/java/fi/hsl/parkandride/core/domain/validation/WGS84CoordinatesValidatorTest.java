package fi.hsl.parkandride.core.domain.validation;

import static fi.hsl.parkandride.core.domain.Spatial.fromWkt;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class WGS84CoordinatesValidatorTest {

    private final WGS84CoordinatesValidator validator = new WGS84CoordinatesValidator();

    @Test
    public void helsinki_is_valid() {
        assertThat(validator.isValid(fromWkt("POINT(24.938466 60.170014)"))).isTrue();
    }

    @Test
    public void wgs84_bounds() {
        assertThat(validator.isValid(fromWkt("POLYGON((-180 -90, -180 90, 180 90, 180 -90, -180 -90))"))).isTrue();
    }

    @Test
    public void invalid_longitude() {
        assertThat(validator.isValid(fromWkt("POINT(-181 -90)"))).isFalse();
        assertThat(validator.isValid(fromWkt("POINT(181 90)"))).isFalse();
    }

    @Test
    public void invalid_latitude() {
        assertThat(validator.isValid(fromWkt("POINT(-180 -90.1)"))).isFalse();
        assertThat(validator.isValid(fromWkt("POINT(180 90.1)"))).isFalse();
    }
}