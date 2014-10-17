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
    public void translates_multiple_keys() {
        assertThat(translator.translate("foo[1].bar.baz[KEY].qux")).isEqualTo("foo[1].bar.baz.KEY.qux");
        assertThat(translator.translate("foo[1].bar.baz[KEY]")).isEqualTo("foo[1].bar.baz.KEY");
    }

    @Test
    public void no_translation_for_indices() {
        assertThat(translator.translate("foo[1].bar")).isEqualTo("foo[1].bar");
        assertThat(translator.translate("foo[1]")).isEqualTo("foo[1]");
    }

    @Test
    public void no_translation_without_key_or_indice() {
        assertThat(translator.translate("foo")).isEqualTo("foo");
        assertThat(translator.translate("foo.bar")).isEqualTo("foo.bar");
    }

    @Test
    public void no_translation_when_empty() {
        assertThat(translator.translate("")).isEqualTo("");
    }

    @Test
    public void no_translation_when_empty_() {
        assertThat(translator.translate("foo[.].bar")).isEqualTo("foo...bar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullInputIsIllegal() {
        translator.translate(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whitespaceInputIsIllegal() {
        translator.translate("  ");
    }

    @Test
    public void knownIssues() {
        assertThat(translator.translate("foo[.].bar")).isEqualTo("foo...bar");
        assertThat(translator.translate("foo[..].bar")).isEqualTo("foo[..].bar");
        assertThat(translator.translate("foo[]].bar")).isEqualTo("foo.].bar");
        assertThat(translator.translate("foo[[].bar")).isEqualTo("foo.[.bar");
    }
}