package fi.hsl.parkandride.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PricingTest {

    @Test
    public void all_nulls() {
        assertThat(new Pricing().compareTo(new Pricing())).isEqualTo(0);
    }

}
