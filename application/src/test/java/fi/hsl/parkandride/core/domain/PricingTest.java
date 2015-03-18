// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PricingTest {

    @Test
    public void all_nulls() {
        assertThat(Pricing.COMPARATOR.compare(new Pricing(), new Pricing())).isEqualTo(0);
    }

}
