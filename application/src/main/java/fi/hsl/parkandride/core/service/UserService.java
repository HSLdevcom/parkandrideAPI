package fi.hsl.parkandride.core.service;

import java.util.Map;

import org.jasypt.util.password.PasswordEncryptor;

import com.google.common.collect.ImmutableMap;

import fi.hsl.parkandride.core.domain.Role;
import fi.hsl.parkandride.core.domain.User;

public class UserService {

    private final Map<String, User> users;

    private final PasswordEncryptor passwordEncryptor;

    public UserService(PasswordEncryptor passwordEncryptor) {
        this.passwordEncryptor = passwordEncryptor;
        users = ImmutableMap.of(
                "admin", new User(1l, "admin", passwordEncryptor.encryptPassword("admin"), Role.ADMIN),
                "operator", new User(1l, "operator", passwordEncryptor.encryptPassword("operator"), Role.OPERATOR)
        );
    }

    public User authenticate(String username, String password) {
        User user = users.get(username.toLowerCase());
        if (user == null) {
            throw new AccessDeniedException();
        }
        if (!passwordEncryptor.checkPassword(password, user.password)) {
            throw new AccessDeniedException();
        }
        return user;
    }

}
