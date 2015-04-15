// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import static fi.hsl.parkandride.core.domain.PropertyPathTranslator.translate;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PropertyPathTranslatorTest {

    @Test
    public void translates_keys() {
        assertThat(translate("foo[KEY].bar")).isEqualTo("foo.KEY.bar");
        assertThat(translate("foo[KEY]")).isEqualTo("foo.KEY");
    }

    @Test
    public void translates_multiple_keys() {
        assertThat(translate("foo[1].bar.baz[KEY].qux")).isEqualTo("foo[1].bar.baz.KEY.qux");
        assertThat(translate("foo[1].bar.baz[KEY]")).isEqualTo("foo[1].bar.baz.KEY");
    }

    @Test
    public void no_translation_for_indices() {
        assertThat(translate("foo[1].bar")).isEqualTo("foo[1].bar");
        assertThat(translate("foo[1]")).isEqualTo("foo[1]");
    }

    @Test
    public void no_translation_without_key_or_index() {
        assertThat(translate("foo")).isEqualTo("foo");
        assertThat(translate("foo.bar")).isEqualTo("foo.bar");
    }

    @Test
    public void no_translation_when_empty() {
        assertThat(translate("")).isEqualTo("");
    }

    @Test
    public void no_translation_when_empty_() {
        assertThat(translate("foo[.].bar")).isEqualTo("foo...bar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullInputIsIllegal() {
        translate(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whitespaceInputIsIllegal() {
        translate("  ");
    }

    @Test
    public void knownIssues() {
        assertThat(translate("foo[.].bar")).isEqualTo("foo...bar");
        assertThat(translate("foo[..].bar")).isEqualTo("foo[..].bar");
        assertThat(translate("foo[]].bar")).isEqualTo("foo.].bar");
        assertThat(translate("foo[[].bar")).isEqualTo("foo.[.bar");
    }
}