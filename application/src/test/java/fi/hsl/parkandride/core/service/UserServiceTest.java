package fi.hsl.parkandride.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.jasypt.util.password.PasswordEncryptor;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import fi.hsl.parkandride.core.back.UserRepository;
import fi.hsl.parkandride.core.domain.NewUser;
import fi.hsl.parkandride.core.domain.Role;
import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.domain.UserSecret;
import fi.hsl.parkandride.core.domain.Violation;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncryptor passwordEncryptor;

    private UserService userService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        userService = new UserService(
                userRepository,
                new AuthenticationService(userRepository, passwordEncryptor, "secret", Period.seconds(60)),
                new ValidationService());
    }

    private final static Long DEFAULT_OPERATOR = 0L;
    private final static String DEFAULT_PASSWORD = "pass";
    private final AtomicLong seq = new AtomicLong(1L);

    private final User adminCreator = creator("admin_creator", Role.ADMIN);
    private final User operatorCreator = creator("operator_creator", Role.OPERATOR);
    private final User operatorAPICreator = creator("operator_api_creator", Role.OPERATOR_API);

    private final NewUser admin = input("admin", Role.ADMIN, DEFAULT_PASSWORD);
    private final NewUser operator = input("operator", Role.OPERATOR, DEFAULT_PASSWORD);
    private final NewUser operatorAPI = input("operator_api", Role.OPERATOR_API, null);

    @Test
    public void password_is_required_for_operator() {
        Runnable createFn = () -> userService.createUser(operator, adminCreator);

        createFn.run();

        operator.password = null;
        assertThat(violations(createFn)).extracting("type", "path").containsOnly(tuple("BadPassword", "password"));

        operator.password = "";
        assertThat(violations(createFn)).extracting("type", "path").containsOnly(tuple("BadPassword", "password"));

        operator.password = " ";
        assertThat(violations(createFn)).extracting("type", "path").containsOnly(tuple("BadPassword", "password"));
    }

    @Test
    public void password_is_not_required_for_operator_api() {
        operatorAPI.password = null;
        userService.createUser(operatorAPI, adminCreator);
    }

    @Test
    public void password_is_required_for_admin() {
        Runnable createFn = () -> userService.createUser(admin, adminCreator);

        createFn.run();

        admin.password = null;
        assertThat(violations(createFn)).extracting("type", "path").containsOnly(tuple("BadPassword", "password"));
    }

    @Test
    public void operator_is_required_for_operator() {
        Runnable createFn = () -> userService.createUser(operator, adminCreator);

        createFn.run();

        operator.operatorId = null;
        assertThat(violations(createFn)).extracting("type", "path").containsOnly(tuple("OperatorRequired", "operator"));
    }

    @Test
    public void operator_is_required_for_operator_api() {
        Runnable createFn = () -> userService.createUser(operatorAPI, adminCreator);

        createFn.run();

        operatorAPI.operatorId = null;
        assertThat(violations(createFn)).extracting("type", "path").containsOnly(tuple("OperatorRequired", "operator"));
    }

    @Test
    public void operator_is_not_allowed_for_admin() {
        Runnable createFn = () -> userService.createUser(admin, adminCreator);

        createFn.run();

        admin.operatorId = DEFAULT_OPERATOR;
        assertThat(violations(createFn)).extracting("type", "path").containsOnly(tuple("OperatorNotAllowed", "operator"));
    }

    @Test
    public void role_is_required() {
        Runnable createFn = () -> userService.createUser(operator, adminCreator);

        operator.role = null;
        assertThat(violations(createFn)).extracting("type", "path").containsOnly(tuple("NotNull", "role"));
    }

    @Test(expected = AccessDeniedException.class)
    public void operator_cannot_create_admin() {
        userService.createUser(admin, operatorCreator);
    }

    @Test(expected = AccessDeniedException.class)
    public void operator_cannot_create_other_operators_user() {
        operatorAPI.operatorId = DEFAULT_OPERATOR + 1;
        userService.createUser(operatorAPI, operatorCreator);
    }

    @Test
    public void operator_api_cannot_create_users() {
        assertAccessDenied(() -> userService.createUser(admin, operatorAPICreator));
        assertAccessDenied(() -> userService.createUser(operator, operatorAPICreator));
        assertAccessDenied(() -> userService.createUser(input("same_operator__other_api", Role.OPERATOR_API, null), operatorAPICreator));
    }

    @Test
    public void api_token_can_be_reset() {
        when(userRepository.getUser(operatorAPI.id)).thenReturn(userSecret(operatorAPI));
        when(userRepository.getCurrentTime()).thenReturn(DateTime.now());

        userService.resetToken(operatorAPI.id, adminCreator);
        userService.resetToken(operatorAPI.id, operatorCreator);
    }

    @Test
    public void non_api_token_cannot_be_reset() {
        when(userRepository.getUser(admin.id)).thenReturn(userSecret(admin));
        when(userRepository.getUser(operator.id)).thenReturn(userSecret(operator));
        when(userRepository.getCurrentTime()).thenReturn(DateTime.now());

        Runnable resetFn = () -> userService.resetToken(admin.id, adminCreator);
        assertThat(violations(resetFn)).extracting("type", "path").containsOnly(tuple("PerpetualTokenNotAllowed", ""));

        resetFn = () -> userService.resetToken(operator.id, operatorCreator);
        assertThat(violations(resetFn)).extracting("type", "path").containsOnly(tuple("PerpetualTokenNotAllowed", ""));
    }

    @Test(expected = AccessDeniedException.class)
    public void operator_cannot_reset_other_operators_api_token() {
        NewUser otherAPI = input("other_operator_api", Role.OPERATOR_API, DEFAULT_PASSWORD);
        otherAPI.operatorId = DEFAULT_OPERATOR + 1;

        when(userRepository.getUser(otherAPI.id)).thenReturn(userSecret(otherAPI));
        when(userRepository.getCurrentTime()).thenReturn(DateTime.now());

        userService.resetToken(otherAPI.id, operatorCreator);
    }

    @Test(expected = AccessDeniedException.class)
    public void operator_api_cannot_reset_tokens() {
        when(userRepository.getUser(operatorAPI.id)).thenReturn(userSecret(operatorAPI));
        userService.resetToken(operatorAPI.id, operatorAPI);
    }

    @Test
    public void operator_can_update_operator_password() {
        when(userRepository.getUser(operator.id)).thenReturn(userSecret(operator));
        userService.updatePassword(operator.id, "newPass", operatorCreator);
    }

    @Test(expected = AccessDeniedException.class)
    public void operator_cannot_update_admin_password() {
        when(userRepository.getUser(admin.id)).thenReturn(userSecret(admin));
        userService.updatePassword(admin.id, "newPass", operatorCreator);
    }

    @Test
    public void admin_can_update_operator_password() {
        when(userRepository.getUser(operator.id)).thenReturn(userSecret(operator));
        userService.updatePassword(operator.id, "newPass", adminCreator);
    }

    @Test
    public void admin_can_update_admin_password() {
        when(userRepository.getUser(admin.id)).thenReturn(userSecret(admin));
        userService.updatePassword(admin.id, "newPass", adminCreator);
    }

    @Test
    public void operator_api_cannot_update_passwords() {
        when(userRepository.getUser(operator.id)).thenReturn(userSecret(operator));
        assertAccessDenied(() -> userService.updatePassword(operator.id, "newPass", operatorAPICreator));
    }

    @Test(expected = AccessDeniedException.class)
    public void operator_cannot_update_other_operators_password() {
        operator.operatorId = DEFAULT_OPERATOR + 1;

        when(userRepository.getUser(operator.id)).thenReturn(userSecret(operator));
        userService.updatePassword(operator.id, "newPass", operatorAPICreator);
    }

    @Test
    public void operator_api_password_cannot_be_updated() {
        Runnable updateFn = () -> userService.updatePassword(operatorAPI.id, "newPass", adminCreator);

        when(userRepository.getUser(operatorAPI.id)).thenReturn(userSecret(operatorAPI));
        assertThat(violations(updateFn)).extracting("type", "path").containsOnly(tuple("PasswordUpdateNotApplicable", ""));
    }

    @Test
    public void password_is_stored_encrypted() {
        when(userRepository.getUser(operator.id)).thenReturn(userSecret(operator));
        when(passwordEncryptor.encryptPassword("newPass")).thenReturn("newPassEncrypted");

        userService.updatePassword(operator.id, "newPass", adminCreator);

        verify(userRepository).updatePassword(operator.id, "newPassEncrypted");
    }

    private UserSecret userSecret(User u) {
        UserSecret us = new UserSecret();
        us.user = u;
        return us;
    }

    private NewUser input(String username, Role role, String pass) {
        NewUser input = new NewUser(seq.incrementAndGet(), username, role, pass);
        if (role != Role.ADMIN) {
            input.operatorId = DEFAULT_OPERATOR;
        }
        return input;
    }

    private User creator(String username, Role role) {
        User creator = new User(seq.incrementAndGet(), username, role);
        if (role != Role.ADMIN) {
            creator.operatorId = DEFAULT_OPERATOR;
        }
        return creator;
    }

    private List<Violation> violations(Runnable r) {
        try {
            r.run();
            throw new AssertionError("did not throw ValidationException");
        } catch (ValidationException e) {
            return e.violations;
        }
    }

    private void assertAccessDenied(Runnable r) {
        try {
            r.run();
            Assert.fail("did not throw AcceddDeniedException");
        } catch (AccessDeniedException expected) {}
    }
}