package fi.hsl.parkandride.core.service;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Strings.isNullOrEmpty;
import static org.joda.time.DateTime.now;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;
import org.jasypt.util.password.PasswordEncryptor;
import org.joda.time.Duration;

import com.google.common.collect.ImmutableMap;

import fi.hsl.parkandride.core.domain.Login;
import fi.hsl.parkandride.core.domain.Role;
import fi.hsl.parkandride.core.domain.User;

public class UserService {

    public static final char PERPETUAL_TOKEN_PREFIX = 'P';

    public static final char TEMPORARY_TOKEN_PREFIX = 'T';

    public static final char DELIM = '|';

    private Map<String, User> usersByUsername;

    private Map<Long, User> usersById;

    private final PasswordEncryptor passwordEncryptor;

    private static final String HMAC = "HmacSHA1";

    private final Base32 base32;

    private final Duration expiresDuration;

    private volatile SecureRandom random;

    private AtomicLong randomCount = new AtomicLong(0);

    private final int reseedInterval = 100;

    // lengths divisible by 5 go nicely with base 32
    private final int secretLength = 20;

    public UserService(PasswordEncryptor passwordEncryptor) {
        this.passwordEncryptor = passwordEncryptor;
        this.expiresDuration = Duration.standardHours(1);
        this.base32 = new Base32(-1);
        dummyUsers(
                new User(1l, "admin", passwordEncryptor.encryptPassword("admin"), Role.ADMIN, "admin"),
                new User(2l, "operator", passwordEncryptor.encryptPassword("operator"), Role.OPERATOR, "operator")
        );
    }

    private void dummyUsers(User... users) {
        usersByUsername = new HashMap<>();
        usersById = new HashMap<>();
        for (User user : users) {
            resetSecret(user);
            usersByUsername.put(user.username, user);
            usersById.put(user.id, user);
        }
    }

    private SecureRandom getSecureRandom() {
        if (randomCount.getAndIncrement() % reseedInterval == 0) {
            SecureRandom sr = null;
            try {
                sr = SecureRandom.getInstance("SHA1PRNG");
                // force SecureRandom to seed itself
                sr.nextBytes(new byte[0]);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            this.random = sr;
        }
        return this.random;
    }

    public Login login(String username, String password) {
        User user = loadUser(username);
        if (!passwordEncryptor.checkPassword(password, user.password)) {
            throw new AuthenticationRequiredException();
        }
        if (user.role.perpetualToken) {
            throw new RuntimeException("Login not allowed for API-user");
        }
        long expires = now().plus(expiresDuration).getMillis();

        Login login = new Login();
        login.token = token(user.secret,
                TEMPORARY_TOKEN_PREFIX,
                user.id,
                Long.toString(expires));
        login.username = user.username;
        login.role = user.role;
        return login;
    }

    public void logout(User user) {
        resetSecret(user);
    }

    private User loadUser(String username) {
        User user = usersByUsername.get(username.toLowerCase());
        if (user == null) {
            throw new AuthenticationRequiredException();
        }
        return user;
    }

    private User loadUser(long id) {
        User user = usersById.get(id);
        if (user == null) {
            throw new AuthenticationRequiredException();
        }
        return user;
    }

    private String token(String secret, Object... components) {
        StringBuilder token = new StringBuilder();
        for (int i=0; i < components.length; i++) {
            token.append(components[i]);
            token.append(DELIM);
        }
        token.append(hmac(secret, token.toString()));
        return token.toString();
    }

    private String resetSecret(User user) {
        byte[] bytes = new byte[secretLength];
        getSecureRandom().nextBytes(bytes);
        user.secret = base32.encodeAsString(bytes);
        // TODO: userDao.update(user);
        return user.secret;
    }

    public String resetPerpetualToken(User user) {
        if (!user.role.perpetualToken) {
            throw new IllegalArgumentException("User's role doesn't allow perpetual token");
        }
        String secret = resetSecret(user);
        return token(secret,
                PERPETUAL_TOKEN_PREFIX,
                user.id);
    }

    private String hmac(String secret, String message) {
        try {
            Mac hmac = Mac.getInstance(HMAC);
            SecretKeySpec key = new SecretKeySpec(base32.decode(secret), "HmacSHA256");
            hmac.init(key);
            byte[] mac = hmac.doFinal(message.getBytes(UTF_8));
            return base32.encodeToString(mac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public User authenticate(String token) {
        if (isNullOrEmpty(token)) {
            throw new AuthenticationRequiredException();
        }
        final int i = token.lastIndexOf(DELIM);
        if (i < 4) {
            // Shortest theoretical message part is: P|u|
            throw new AuthenticationRequiredException();
        }
        if (i + 1 == token.length()) {
            // MAC expected
            throw new AuthenticationRequiredException();
        }
        if (token.charAt(1) != DELIM) {
            // DELIM expected
            throw new AuthenticationRequiredException();
        }

        final int u = token.indexOf(DELIM, 2);
        if (u < 3) {
            // user id expected
            throw new AuthenticationRequiredException();
        }

        try {
            String userId = token.substring(2, u);
            User user = loadUser(Long.valueOf(userId));

            if (!user.role.perpetualToken) {
                if (token.charAt(0) != TEMPORARY_TOKEN_PREFIX) {
                    throw new AuthenticationRequiredException();
                }
                long expires = Long.valueOf(token.substring(3 + userId.length(), i));
                if (expires < now().getMillis()) {
                    throw new AuthenticationRequiredException();
                }
            } else {
                if (token.charAt(0) != PERPETUAL_TOKEN_PREFIX) {
                    throw new AuthenticationRequiredException();
                }
                if (u != i) {
                    throw new AuthenticationRequiredException();
                }
            }

            String givenMac = token.substring(i + 1);
            String expectedMac = hmac(user.secret, token.substring(0, i + 1));

            if (!expectedMac.equals(givenMac)) {
                throw new AuthenticationRequiredException();
            }

            return user;
        } catch (NumberFormatException e) {
            throw new AuthenticationRequiredException();
        }
    }

}
