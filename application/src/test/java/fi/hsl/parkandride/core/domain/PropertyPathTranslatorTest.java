package fi.hsl.parkandride.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PropertyPathTranslatorTest {
    private final PropertyPathTranslator translator = new PropertyPathTranslator();

    @Test
    public void translates_keys() {
        assertThat(translator.translate("foo[KEY].bar")).isEqualTo("foo.KEY.bar");
        assertThat(translator.translate("foo[KEY]")).isEqualTo("foo.KEY");
    }

    @Test
    public void translates_indices() {
        assertThat(translator.translate("foo[1].bar")).isEqualTo("foo.1.bar");
        assertThat(translator.translate("foo[1]")).isEqualTo("foo.1");
    }

    @Test
    public void translates_multiple_keys_and_indices() {
        assertThat(translator.translate("foo[1].bar.baz[KEY].qux")).isEqualTo("foo.1.bar.baz.KEY.qux");
        assertThat(translator.translate("foo[1].bar.baz[KEY]")).isEqualTo("foo.1.bar.baz.KEY");
    }

    @Test
    public void no_translation_without_key_or_indice() {
        assertThat(translator.translate("foo")).isEqualTo("foo");
        assertThat(translator.translate("foo.bar")).isEqualTo("foo.bar");
    }

    @Test
    public void no_translation_when_blank() {
        assertThat(translator.translate(null)).isNull();
        assertThat(translator.translate("")).isEqualTo("");
        assertThat(translator.translate("  ")).isEqualTo("  ");
    }
}