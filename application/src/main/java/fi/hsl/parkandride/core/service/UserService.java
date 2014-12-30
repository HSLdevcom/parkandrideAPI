package fi.hsl.parkandride.core.service;

import static fi.hsl.parkandride.core.domain.Permission.USER_CREATE;
import static fi.hsl.parkandride.core.domain.Permission.USER_VIEW;
import static fi.hsl.parkandride.core.domain.Role.ADMIN;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR_API;
import static fi.hsl.parkandride.core.service.AuthenticationService.authorize;

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

        if (currentUser.role != ADMIN && !isOperatorRole(newUser.role)) {
            throw new ValidationException(new Violation("IllegalRole", "role", "Expected an operator role, got " + newUser.role));
        }

        validationService.validate(newUser);
        return createUserNoValidate(newUser);
    }

    @TransactionalWrite
    public User createUserNoValidate(NewUser newUser) {
        UserSecret userSecret = new UserSecret();
        if (!newUser.role.perpetualToken) {
            validatePassword(newUser.password);
            userSecret.password = authenticationService.encryptPassword(newUser.password);
        }
        userSecret.user = new User(newUser);
        userSecret.user.id = userRepository.insertUser(userSecret);
        return userSecret.user;
    }

    private void validatePassword(String password) {
        if (!isValidPassword(password)) {
            throw new ValidationException(new Violation("BadPassword", "password", "Expected a password of length >= 8"));
        }
    }

    private boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        // TODO
        return true;
    }

    private boolean isOperatorRole(Role role) {
        return role == OPERATOR || role == OPERATOR_API;
    }
}
