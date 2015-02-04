package fi.hsl.parkandride.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.Test;

public class MultilingualStringTest {

    @Test
    public void not_equals() {
        assertThat(new MultilingualString("test").equals(new Object())).isEqualTo(false);
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
        Map<String, String> map = str.asMap();

        map.put("fi", "Finnish");
        assertThat(str.getFi()).isEqualTo("Finnish");

        map.put("sv", "Swedish");
        assertThat(str.getSv()).isEqualTo("Swedish");

        map.put("en", "English");
        assertThat(str.getEn()).isEqualTo("English");
    }
}
