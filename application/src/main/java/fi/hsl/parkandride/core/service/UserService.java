package fi.hsl.parkandride.core.service;

import static fi.hsl.parkandride.core.domain.Permission.USER_CREATE;
import static fi.hsl.parkandride.core.domain.Permission.USER_UPDATE;
import static fi.hsl.parkandride.core.domain.Permission.USER_VIEW;
import static fi.hsl.parkandride.core.domain.Role.ADMIN;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR_API;
import static fi.hsl.parkandride.core.service.AuthenticationService.authorize;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.util.StringUtils;

import fi.hsl.parkandride.core.back.UserRepository;
import fi.hsl.parkandride.core.domain.*;

public class UserService {

    private final AuthenticationService authenticationService;

    private final UserRepository userRepository;

    private final ValidationService validationService;

    public UserService(UserRepository userRepository, AuthenticationService authenticationService, ValidationService validationService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.validationService = validationService;
    }

    public SearchResults<User> findUsers(UserSearch search, User currentUser) {
        authorize(currentUser, search, USER_VIEW);

        return userRepository.findUsers(search);
    }


    @TransactionalWrite
    public User createUser(NewUser newUser, User currentUser) {
        authorize(currentUser, newUser, USER_CREATE);

        validate(currentUser, newUser);
        return createUserNoValidate(newUser);
    }

    @TransactionalWrite
    public User createUserNoValidate(NewUser newUser) {
        UserSecret userSecret = new UserSecret();
        if (!newUser.role.perpetualToken) {
            userSecret.password = authenticationService.encryptPassword(newUser.password);
        }
        userSecret.user = new User(newUser);
        userSecret.user.id = userRepository.insertUser(userSecret);
        return userSecret.user;
    }

    @TransactionalWrite
    public String resetToken(Long userId, User currentUser) {
        authorize(currentUser, userRepository.getUser(userId).user, USER_UPDATE);
        return authenticationService.resetToken(userId);
    }

    @TransactionalWrite
    public void updatePassword(Long userId, String newPassword, User updater) {
        User user = userRepository.getUser(userId).user;
        authorize(updater, user, USER_UPDATE);

        // TODO don't allow api user
        userRepository.updatePassword(userId, authenticationService.encryptPassword(newPassword));
    }

    private void validate(User currentUser, NewUser newUser) {
        Collection<Violation> violations = new ArrayList<>();

        if (!isAdminRole(currentUser.role) && !isOperatorRole(newUser.role)) {
            violations.add(new Violation("IllegalRole", "role", "Expected an operator role, got " + newUser.role));
        }

        validationService.validate(newUser, violations);
        // cannot continue if e.g. role is not provided
        if (violations.isEmpty()) {
            if (!newUser.role.perpetualToken) {
                validatePassword(newUser.password, violations);
            }
            validateOperator(newUser, violations);
        }

        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }
    }

    private void validateOperator(NewUser newUser, Collection<Violation> violations) {
        if (isOperatorRole(newUser.role) && newUser.operatorId == null) {
            violations.add(new Violation("OperatorRequired", "operator", "Operator is required for operator user"));
        }

        if (isAdminRole(newUser.role) && newUser.operatorId != null) {
            violations.add(new Violation("OperatorNotAllowed", "operator", "Operator is not allowed for admin user"));
        }
    }

    private void validatePassword(String password, Collection<Violation> violations) {
        if (!isValidPassword(password)) {
            violations.add(new Violation("BadPassword", "password", "Expected a password of length >= 8"));
        }
    }

    private boolean isValidPassword(String password) {
        if (!StringUtils.hasText(password)) {
            return false;
        }
        // TODO
        return true;
    }

    private boolean isOperatorRole(Role role) {
        return role == OPERATOR || role == OPERATOR_API;
    }

    private boolean isAdminRole(Role role) {
        return role == ADMIN;
    }
}
