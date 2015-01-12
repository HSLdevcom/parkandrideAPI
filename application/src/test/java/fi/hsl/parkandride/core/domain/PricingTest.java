package fi.hsl.parkandride.core.domain;

import static fi.hsl.parkandride.core.domain.Pricing.COMPARATOR;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PricingTest {

    @Test
    public void all_nulls() {
        assertThat(COMPARATOR.compare(new Pricing(), new Pricing())).isEqualTo(0);
    }
    
}
