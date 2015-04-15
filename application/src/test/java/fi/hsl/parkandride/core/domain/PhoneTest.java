// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class PhoneTest {

    @Test
    public void cellphone() {
        String number = new Phone("456563001").toString();
        assertThat(number).isEqualTo("045 6563001");
        assertThat(new Phone("+358 45 656 3001").toString()).isEqualTo(number);
    }

    @Test
    public void landline() {
        String number = new Phone("(09) 58422288").toString();
        assertThat(number).isEqualTo("09 58422288");
    }

    @Test
    public void international_number() {
        String number = new Phone("+44 (0)343 222 2222").toString();
        assertThat(number).isEqualTo("+44 343 222 2222");
    }

    @Test(expected = IllegalArgumentException.class)
    public void too_short_number() {
        new Phone("0800");
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegal_area_code() {
        new Phone("011 123456");
    }

}
