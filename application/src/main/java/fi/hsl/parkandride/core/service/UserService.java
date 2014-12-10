package fi.hsl.parkandride.core.service;

import static fi.hsl.parkandride.core.domain.Role.ADMIN;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR_API;

import fi.hsl.parkandride.core.back.UserRepository;
import fi.hsl.parkandride.core.domain.*;

public class UserService {

    private final AuthService authService;

    private final AuthenticationService authenticationService;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, AuthService authService, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.authenticationService = authenticationService;
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
                throw new ValidationException(new Violation("IllegalRole", "role", "Illegal operator role: " + newUser.role));
            }
        }

        UserSecret userSecret = new UserSecret();
        userSecret.password = authenticationService.encryptPassword(newUser.password);
        userSecret.secret = authenticationService.newSecret();
        userSecret.user = newUser;
        userSecret.user.id = userRepository.insertUser(userSecret);
        return userSecret.user;
    }

    private boolean isOperatorRole(Role role) {
        return role == OPERATOR || role == OPERATOR_API;
    }
}
