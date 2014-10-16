package fi.hsl.parkandride.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PropertyPathTranslatorTest {
    private final PropertyPathTranslator translator = new PropertyPathTranslator();

    @Test
    public void translates_keys() {
        assertThat(translator.translate("foo[KEY].bar")).isEqualTo("foo.KEY.bar");
    }

    @Test
    public void translates_indices() {
        assertThat(translator.translate("foo[1].bar")).isEqualTo("foo.1.bar");
    }

    @Test
    public void no_translation_without_key_or_indice() {
        assertThat(translator.translate("foo.bar")).isEqualTo("foo.bar");
    }

    @Test
    public void no_translation_when_blank() {
        assertThat(translator.translate(null)).isNull();
        assertThat(translator.translate("")).isEqualTo("");
        assertThat(translator.translate("  ")).isEqualTo("  ");
    }
}