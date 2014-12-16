package fi.hsl.parkandride.core.service;

import static fi.hsl.parkandride.core.domain.Role.ADMIN;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR_API;
import static fi.hsl.parkandride.core.service.AuthenticationService.TOKEN_PATTERN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.regex.Matcher;

import org.jasypt.util.password.PasswordEncryptor;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Period;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fi.hsl.parkandride.core.back.UserRepository;
import fi.hsl.parkandride.core.domain.Login;
import fi.hsl.parkandride.core.domain.NotFoundException;
import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.domain.UserSecret;

public class AuthenticationServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncryptor passwordEncryptor;

    private AuthenticationService service;

    private final static int EXPIRES_IN_SECONDS = 30;

    private final static Period expires = Period.seconds(EXPIRES_IN_SECONDS);

    private final UserSecret adminUser = new UserSecret(1l, "admin", "admin-password", ADMIN);

    private final UserSecret apiUser = new UserSecret(2l, "api", null, OPERATOR_API);

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        this.service = new AuthenticationService(userRepository, passwordEncryptor, "salaisuus", expires);
    }

    @After
    public void resetTime() {
        DateTimeUtils.setCurrentMillisSystem();
    }

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

    @Test(expected = ValidationException.class)
    public void login_with_bad_username() {
        when(userRepository.getUser("username")).thenThrow(NotFoundException.class);
        service.login("username", "whatever");
    }

    @Test(expected = ValidationException.class)
    public void login_with_api_role_should_not_be_allowed() {
        when(userRepository.getUser("api")).thenReturn(apiUser);
        service.login("api", "whatever");
    }

    @Test(expected = ValidationException.class)
    public void login_with_bac_credentials() {
        when(userRepository.getUser("admin")).thenReturn(adminUser);
        when(passwordEncryptor.checkPassword("admin-password", "admin-password")).thenReturn(false);
        service.login("admin", "admin-password");
    }

    @Test
    public void login_and_authenticate() {
        when(userRepository.getUser("admin")).thenReturn(adminUser);
        when(passwordEncryptor.checkPassword("admin-password", "admin-password")).thenReturn(true);
        Login login = service.login("admin", "admin-password");

        assertThat(login.token).startsWith("T");
        assertThat(login.username).isEqualTo("admin");
        assertThat(login.role).isEqualTo(ADMIN);

        when(userRepository.getUser(1l)).thenReturn(adminUser);

        User user = service.authenticate(login.token);
        assertThat(user).isSameAs(adminUser.user);
    }

    @Test(expected = AuthenticationRequiredException.class)
    public void expired_token() {
        when(userRepository.getUser("admin")).thenReturn(adminUser);
        when(passwordEncryptor.checkPassword("admin-password", "admin-password")).thenReturn(true);
        Login login = service.login("admin", "admin-password");
        DateTimeUtils.setCurrentMillisOffset(EXPIRES_IN_SECONDS * 1001);

        service.authenticate(login.token);
    }

    @Test(expected = AuthenticationRequiredException.class)
    public void missing_token() {
        service.authenticate(null);
    }

    @Test(expected = AuthenticationRequiredException.class)
    public void invalid_token_format() {
        service.authenticate("T:123:456:xyz");
    }

    @Test(expected = AuthenticationRequiredException.class)
    public void invalid_hmac() {
        when(userRepository.getUser("admin")).thenReturn(adminUser);
        when(passwordEncryptor.checkPassword("admin-password", "admin-password")).thenReturn(true);
        Login login = service.login("admin", "admin-password");
        service.authenticate("P" + login.token.substring(1));
    }

    @Test(expected = AuthenticationRequiredException.class)
    public void token_type_mismatch() {
        when(userRepository.getUser("admin")).thenReturn(adminUser);
        when(passwordEncryptor.checkPassword("admin-password", "admin-password")).thenReturn(true);
        Login login = service.login("admin", "admin-password");

        when(userRepository.getUser(1l)).thenReturn(apiUser);
        service.authenticate(login.token);
    }

    @Test(expected = AuthenticationRequiredException.class)
    public void revoked_token() {
        when(userRepository.getUser("admin")).thenReturn(adminUser);
        when(passwordEncryptor.checkPassword("admin-password", "admin-password")).thenReturn(true);
        Login login = service.login("admin", "admin-password");
        adminUser.minTokenTimestamp = new DateTime().plus(1);

        when(userRepository.getUser(1l)).thenReturn(adminUser);
        service.authenticate(login.token);
    }

    @Test
    public void perpetual_token_doesnt_expire() {
        when(userRepository.getUser(2l)).thenReturn(apiUser);
        DateTime now = DateTime.now();
        when(userRepository.getCurrentTime()).thenReturn(now);

        String token = service.resetToken(2l);
        verify(userRepository).revokeTokens(2l, now);

        DateTimeUtils.setCurrentMillisOffset(now.plusYears(1).getMillis());

        User user = service.authenticate(token);
        assertThat(user).isSameAs(apiUser.user);
    }
}
