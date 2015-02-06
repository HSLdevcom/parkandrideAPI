package fi.hsl.parkandride.core.service;

import static fi.hsl.parkandride.core.domain.Permission.ALL_OPERATORS;
import static fi.hsl.parkandride.core.domain.Permission.USER_CREATE;
import static fi.hsl.parkandride.core.domain.Permission.USER_UPDATE;
import static fi.hsl.parkandride.core.domain.Permission.USER_VIEW;
import static fi.hsl.parkandride.core.service.AuthenticationService.authorize;

import java.util.ArrayList;
import java.util.Collection;

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

    @TransactionalRead
    public SearchResults<User> findUsers(UserSearch search, User currentUser) {
        authorize(currentUser, search, USER_VIEW);

        return userRepository.findUsers(search);
    }

    @TransactionalWrite
    public User createUser(NewUser newUser, User actor) {
        authorize(actor, newUser, USER_CREATE);

        validate(newUser, actor);
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
    public String resetToken(Long userId, User actor) {
        authorize(actor, userRepository.getUser(userId).user, USER_UPDATE);
        return authenticationService.resetToken(userId);
    }

    @TransactionalWrite
    public void updatePassword(Long userId, String newPassword, User actor) {
        User user = userRepository.getUser(userId).user;
        authorize(actor, user, USER_UPDATE);

        if (user.role == Role.OPERATOR_API) {
            throw new ValidationException(new Violation("PasswordUpdateNotApplicable", "", "Password update is not applicable for api user"));
        }

        PasswordValidator.validate(newPassword);

        userRepository.updatePassword(userId, authenticationService.encryptPassword(newPassword));
    }

    @TransactionalWrite
    public void deleteUser(long userId, User actor) {
        User user = userRepository.getUser(userId).user;
        authorize(actor, user, USER_UPDATE);

        // don't allow suicide
        if (userId == actor.id) {
            throw new AccessDeniedException();
        }

        userRepository.deleteUser(userId);
    }

    private void validate(NewUser newUser, User actor) {
        Collection<Violation> violations = new ArrayList<>();

        if (newUser.hasPermission(ALL_OPERATORS) && !actor.hasPermission(ALL_OPERATORS)) {
            violations.add(new Violation("IllegalRole", "role", "Expected an operator role, got " + newUser.role));
        }

        validationService.validate(newUser, violations);
        // cannot continue if e.g. role is not provided
        if (violations.isEmpty()) {
            if (!newUser.role.perpetualToken) {
                PasswordValidator.validate(newUser.password, violations);
            }
            validateOperator(newUser, violations);
        }

        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }
    }

    private void validateOperator(NewUser newUser, Collection<Violation> violations) {
        if (newUser.hasPermission(ALL_OPERATORS)) {
            if (newUser.operatorId != null) {
                violations.add(new Violation("OperatorNotAllowed", "operator", "Operator is not allowed for admin user"));
            }
        } else if (newUser.operatorId == null) {
            violations.add(new Violation("OperatorRequired", "operator", "Operator is required for operator user"));
        }
    }

}
