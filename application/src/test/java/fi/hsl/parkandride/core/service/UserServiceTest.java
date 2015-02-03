package fi.hsl.parkandride.core.service;

import static fi.hsl.parkandride.core.ViolationAssert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicLong;

import org.jasypt.util.password.PasswordEncryptor;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fi.hsl.parkandride.core.back.UserRepository;
import fi.hsl.parkandride.core.domain.NewUser;
import fi.hsl.parkandride.core.domain.Role;
import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.domain.UserSecret;

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

    private final User adminActor = creator("admin_actor", Role.ADMIN);
    private final User operatorActor = creator("operator_actor", Role.OPERATOR);
    private final User operatorAPIActor = creator("operator_api_actor", Role.OPERATOR_API);

    private final NewUser admin = input("admin", Role.ADMIN, DEFAULT_PASSWORD);
    private final NewUser operator = input("operator", Role.OPERATOR, DEFAULT_PASSWORD);
    private final NewUser operatorAPI = input("operator_api", Role.OPERATOR_API, null);

    @Test
    public void password_is_required_for_operator() {
        Runnable createFn = () -> userService.createUser(operator, adminActor);

        createFn.run();
        verify(userRepository).insertUser(anyObject());

        operator.password = null;
        assertBadPassword(createFn);

        operator.password = "";
        assertBadPassword(createFn);

        operator.password = " ";
        assertBadPassword(createFn);
    }

    @Test
    public void password_is_not_required_for_operator_api() {
        operatorAPI.password = null;
        userService.createUser(operatorAPI, adminActor);
        verify(userRepository).insertUser(anyObject());
    }

    @Test
    public void password_is_required_for_admin() {
        Runnable createFn = () -> userService.createUser(admin, adminActor);

        createFn.run();
        verify(userRepository).insertUser(anyObject());

        admin.password = null;
        assertBadPassword(createFn);
    }

    @Test
    public void operator_is_required_for_operator() {
        Runnable createFn = () -> userService.createUser(operator, adminActor);

        createFn.run();
        verify(userRepository).insertUser(anyObject());

        operator.operatorId = null;
        assertOperatorRequired(createFn);
    }

    @Test
    public void operator_is_required_for_operator_api() {
        Runnable createFn = () -> userService.createUser(operatorAPI, adminActor);

        createFn.run();
        verify(userRepository).insertUser(anyObject());

        operatorAPI.operatorId = null;
        assertOperatorRequired(createFn);
    }

    @Test
    public void operator_is_not_allowed_for_admin() {
        Runnable createFn = () -> userService.createUser(admin, adminActor);

        createFn.run();
        verify(userRepository).insertUser(anyObject());

        admin.operatorId = DEFAULT_OPERATOR;
        assertOperatorNotAllowed(createFn);
    }

    @Test
    public void role_is_required() {
        operator.role = null;
        assertNotNull(() -> userService.createUser(operator, adminActor));
    }

    @Test(expected = AccessDeniedException.class)
    public void operator_cannot_create_admin() {
        userService.createUser(admin, operatorActor);
    }

    @Test(expected = AccessDeniedException.class)
    public void operator_cannot_create_other_operators_user() {
        operatorAPI.operatorId = DEFAULT_OPERATOR + 1;
        userService.createUser(operatorAPI, operatorActor);
    }

    @Test
    public void operator_api_cannot_create_users() {
        assertAccessDenied(() -> userService.createUser(admin, operatorAPIActor));
        assertAccessDenied(() -> userService.createUser(operator, operatorAPIActor));
        assertAccessDenied(() -> userService.createUser(input("same_operator__other_api", Role.OPERATOR_API, null), operatorAPIActor));
    }

    @Test
    public void api_token_can_be_reset() {
        DateTime now = DateTime.now();

        when(userRepository.getUser(operatorAPI.id)).thenReturn(userSecret(operatorAPI));
        when(userRepository.getCurrentTime()).thenReturn(now);

        userService.resetToken(operatorAPI.id, adminActor);
        userService.resetToken(operatorAPI.id, operatorActor);
        verify(userRepository, times(2)).revokeTokens(operatorAPI.id, now);
    }

    @Test
    public void non_api_token_cannot_be_reset() {
        when(userRepository.getUser(admin.id)).thenReturn(userSecret(admin));
        when(userRepository.getUser(operator.id)).thenReturn(userSecret(operator));
        when(userRepository.getCurrentTime()).thenReturn(DateTime.now());

        assertPerpetualTokenNotAllowed(() -> userService.resetToken(admin.id, adminActor));
        assertPerpetualTokenNotAllowed(() -> userService.resetToken(operator.id, operatorActor));
    }

    @Test(expected = AccessDeniedException.class)
    public void operator_cannot_reset_other_operators_api_token() {
        NewUser otherAPI = input("other_operator_api", Role.OPERATOR_API, DEFAULT_PASSWORD);
        otherAPI.operatorId = DEFAULT_OPERATOR + 1;

        when(userRepository.getUser(otherAPI.id)).thenReturn(userSecret(otherAPI));
        when(userRepository.getCurrentTime()).thenReturn(DateTime.now());

        userService.resetToken(otherAPI.id, operatorActor);
    }

    @Test(expected = AccessDeniedException.class)
    public void operator_api_cannot_reset_tokens() {
        when(userRepository.getUser(operatorAPI.id)).thenReturn(userSecret(operatorAPI));
        userService.resetToken(operatorAPI.id, operatorAPI);
    }

    @Test
    public void operator_can_update_operator_password() {
        when(userRepository.getUser(operator.id)).thenReturn(userSecret(operator));
        when(passwordEncryptor.encryptPassword("newPass")).thenReturn("newPassEncrypted");
        userService.updatePassword(operator.id, "newPass", operatorActor);

        verify(userRepository).updatePassword(operator.id, "newPassEncrypted");
    }

    @Test(expected = AccessDeniedException.class)
    public void operator_cannot_update_admin_password() {
        when(userRepository.getUser(admin.id)).thenReturn(userSecret(admin));
        userService.updatePassword(admin.id, "newPass", operatorActor);
    }

    @Test
    public void admin_can_update_operator_password() {
        when(userRepository.getUser(operator.id)).thenReturn(userSecret(operator));
        when(passwordEncryptor.encryptPassword("newPass")).thenReturn("newPassEncrypted");
        userService.updatePassword(operator.id, "newPass", adminActor);

        verify(userRepository).updatePassword(operator.id, "newPassEncrypted");
    }

    @Test
    public void admin_can_update_admin_password() {
        when(userRepository.getUser(admin.id)).thenReturn(userSecret(admin));
        when(passwordEncryptor.encryptPassword("newPass")).thenReturn("newPassEncrypted");
        userService.updatePassword(admin.id, "newPass", adminActor);

        verify(userRepository).updatePassword(admin.id, "newPassEncrypted");
    }

    @Test(expected = AccessDeniedException.class)
    public void operator_api_cannot_update_passwords() {
        when(userRepository.getUser(operator.id)).thenReturn(userSecret(operator));
        userService.updatePassword(operator.id, "newPass", operatorAPIActor);
    }

    @Test(expected = AccessDeniedException.class)
    public void operator_cannot_update_other_operators_password() {
        operator.operatorId = DEFAULT_OPERATOR + 1;

        when(userRepository.getUser(operator.id)).thenReturn(userSecret(operator));
        userService.updatePassword(operator.id, "newPass", operatorActor);
    }

    @Test
    public void operator_api_password_cannot_be_updated() {
        when(userRepository.getUser(operatorAPI.id)).thenReturn(userSecret(operatorAPI));
        assertPasswordUpdateNotApplicable(() -> userService.updatePassword(operatorAPI.id, "newPass", adminActor));
    }

    @Test
    public void admin_can_delete_other_admin() {
        NewUser otherAdmin = input("other_admin", Role.ADMIN, DEFAULT_PASSWORD);
        when(userRepository.getUser(otherAdmin.id)).thenReturn(userSecret(otherAdmin));

        userService.deleteUser(otherAdmin.id, adminActor);

        verify(userRepository).deleteUser(otherAdmin.id);
    }

    @Test
    public void admin_can_delete_operator() {
        when(userRepository.getUser(operator.id)).thenReturn(userSecret(operator));

        userService.deleteUser(operator.id, adminActor);

        verify(userRepository).deleteUser(operator.id);
    }

    @Test(expected = AccessDeniedException.class)
    public void operator_cannot_delete_admin() {
        when(userRepository.getUser(admin.id)).thenReturn(userSecret(admin));

        userService.deleteUser(admin.id, operatorActor);
    }

    @Test
    public void operator_can_delete_other_user_of_the_same_operator() {
        when(userRepository.getUser(operatorAPI.id)).thenReturn(userSecret(operatorAPI));

        userService.deleteUser(operatorAPI.id, operatorActor);

        verify(userRepository).deleteUser(operatorAPI.id);
    }

    @Test(expected = AccessDeniedException.class)
    public void operator_cannot_delete_other_operators_user() {
        operatorAPI.operatorId = DEFAULT_OPERATOR + 1;
        when(userRepository.getUser(operatorAPI.id)).thenReturn(userSecret(operatorAPI));

        userService.deleteUser(operatorAPI.id, operatorActor);
    }

    @Test(expected = AccessDeniedException.class)
    public void operator_api_cannot_delete_users() {
        when(userRepository.getUser(operator.id)).thenReturn(userSecret(operator));
        userService.deleteUser(operator.id, operatorAPIActor);
    }

    @Test
    public void user_cannot_delete_itself() {
        when(userRepository.getUser(admin.id)).thenReturn(userSecret(admin));
        when(userRepository.getUser(operator.id)).thenReturn(userSecret(operator));

        assertAccessDenied(()->userService.deleteUser(admin.id, admin));
        assertAccessDenied(() -> userService.deleteUser(operator.id, operator));
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

    private static void assertAccessDenied(Runnable r) {
        try {
            r.run();
            Assert.fail("did not throw AccessDeniedException");
        } catch (AccessDeniedException expected) {}
    }
}