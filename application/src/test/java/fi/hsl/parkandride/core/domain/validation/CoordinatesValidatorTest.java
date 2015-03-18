// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain.validation;

import static fi.hsl.parkandride.core.domain.Spatial.fromWkt;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class CoordinatesValidatorTest {

    private final CoordinatesValidator validator = new CoordinatesValidator();

    @Test
    public void helsinki_is_valid() {
        assertThat(validator.isValid(fromWkt("POINT(24.938466 60.170014)"))).isTrue();
    }

    @Test
    public void validity_bounds() {
        assertThat(validator.isValid(fromWkt("POLYGON((19 59.5, 19 70.5, 32 70.5, 32 59.5, 19 59.5))"))).isTrue();
    }

    @Test
    public void invalid_longitude() {
        assertThat(validator.isValid(fromWkt("POINT(18 59.5)"))).isFalse();
        assertThat(validator.isValid(fromWkt("POINT(32.5 70.5)"))).isFalse();
    }

    @Test
    public void invalid_latitude() {
        assertThat(validator.isValid(fromWkt("POINT(19 59)"))).isFalse();
        assertThat(validator.isValid(fromWkt("POINT(32 71)"))).isFalse();
    }
}