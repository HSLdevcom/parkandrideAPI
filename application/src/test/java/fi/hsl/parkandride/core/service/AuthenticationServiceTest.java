package fi.hsl.parkandride.core.service;

import static fi.hsl.parkandride.core.service.AuthenticationService.TOKEN_PATTERN;

import java.util.regex.Matcher;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class AuthenticationServiceTest {

    @Test
    public void token_pattern_regexp() {
        Matcher matcher = TOKEN_PATTERN.matcher("T|123|456|xyz");
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group("message")).isEqualTo("T|123|456|");
        assertThat(matcher.group("type")).isEqualTo("T");
        assertThat(matcher.group("userId")).isEqualTo("123");
        assertThat(matcher.group("timestamp")).isEqualTo("456");
        assertThat(matcher.group("hmac")).isEqualTo("xyz");
    }
}
