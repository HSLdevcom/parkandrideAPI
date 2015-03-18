// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class MultilingualStringTest {
    @Test
    public void equals_and_hashcode() {
        EqualsVerifier.forExamples(new MultilingualString(), new MultilingualString("foo"), new MultilingualString("fi", "sv", "en"))
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

    @Test(expected = IllegalArgumentException.class)
    public void unsupported_language() {
        assertThat(new MultilingualString().asMap().put("foo", "exception"));
    }

    @Test
    public void as_map_read() {
        Map<String, String> map = new MultilingualString("Finnish", "Swedish", "English").asMap();
        assertThat(map.get("fi")).isEqualTo("Finnish");
        assertThat(map.get("sv")).isEqualTo("Swedish");
        assertThat(map.get("en")).isEqualTo("English");
        assertThat(map.get("foo")).isNull();
    }

    @Test
    public void as_map_modify() {
        MultilingualString str = new MultilingualString();
        assertThat(str.toString()).isEqualTo("MultilingualString{fi=null, sv=null, en=null}");

        Map<String, String> map = str.asMap();

        map.put("fi", "Finnish");
        assertThat(str.getFi()).isEqualTo("Finnish");

        map.put("sv", "Swedish");
        assertThat(str.getSv()).isEqualTo("Swedish");

        map.put("en", "English");
        assertThat(str.getEn()).isEqualTo("English");

        assertThat(str.toString()).isEqualTo("MultilingualString{fi=Finnish, sv=Swedish, en=English}");
    }
}
