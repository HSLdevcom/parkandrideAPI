package fi.hsl.parkandride.core.service;

import static com.google.common.base.Strings.isNullOrEmpty;
import static fi.hsl.parkandride.core.domain.Role.ADMIN;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR_API;

import com.google.common.base.Strings;

import fi.hsl.parkandride.core.back.UserRepository;
import fi.hsl.parkandride.core.domain.*;

public class UserService {

    private final AuthService authService;

    private final AuthenticationService authenticationService;

    private final UserRepository userRepository;

    private final ValidationService validationService;

    public UserService(UserRepository userRepository, AuthService authService, AuthenticationService authenticationService, ValidationService validationService) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.authenticationService = authenticationService;
        this.validationService = validationService;
    }

    public SearchResults<User> findUsers(UserSearch search, User currentUser) {
        authService.authorize(currentUser, ADMIN, OPERATOR);

        if (currentUser.role == OPERATOR) {
            search.operatorId = currentUser.operatorId;
        }
        return userRepository.findUsers(search);
    }


    @TransactionalWrite
    public User createUser(NewUser newUser, User currentUser) {
        authService.authorize(currentUser, ADMIN, OPERATOR);

        if (currentUser.role != ADMIN) {
            newUser.operatorId = currentUser.operatorId;
            if (!isOperatorRole(newUser.role)) {
                throw new ValidationException(new Violation("IllegalRole", "role", "Expected an operator role, got " + newUser.role));
            }
        }
        validationService.validate(newUser);

        UserSecret userSecret = new UserSecret();
        if (!newUser.role.perpetualToken) {
            validatePassword(newUser.password);
            userSecret.password = authenticationService.encryptPassword(newUser.password);
        }
        userSecret.user = newUser;
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

        return true;
    }

    private boolean isOperatorRole(Role role) {
        return role == OPERATOR || role == OPERATOR_API;
    }
}
