package fi.hsl.parkandride.core.service;

import static com.google.common.base.Charsets.UTF_8;
import static org.joda.time.DateTime.now;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.util.password.PasswordEncryptor;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;

import fi.hsl.parkandride.core.back.UserRepository;
import fi.hsl.parkandride.core.domain.Login;
import fi.hsl.parkandride.core.domain.NotFoundException;
import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.domain.UserSecret;
import fi.hsl.parkandride.core.domain.Violation;

public class AuthenticationService {

    public static final char DELIM = '|';

    private static final String DELIM_REGEX = "\\|";

    public static final Pattern TOKEN_PATTERN = Pattern.compile(
            "^" + // start
            "(?<message>" + // message for hmac
            "(?<type>[PT])" + DELIM_REGEX + // token type
            "(?<userId>\\d+)" + DELIM_REGEX + // userId
            "(?<timestamp>\\d+)" + DELIM_REGEX + // timestamp
            ")" + // (message for hmac)
            "(?<hmac>[A-Za-z0-9\\-_]+)" +   // base64url(hmac)
            "$" // end
    );

    private final PasswordEncryptor passwordEncryptor;

    private static final String HMAC = "HmacSHA256";

    private final Base64 base64;

    private final Period expiresDuration;

    private final byte[] secret;

    private final ThreadLocal<Mac> hmacHolder = new ThreadLocal<Mac>() {
        @Override
        protected Mac initialValue() {
            try {
                SecretKeySpec key = new SecretKeySpec(secret, HMAC);
                Mac hmac = Mac.getInstance(HMAC);
                hmac.init(key);
                return hmac;
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        }
    };

    private UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository, PasswordEncryptor passwordEncryptor, String secret, Period expiresDuration) {
        this.userRepository = userRepository;
        this.passwordEncryptor = passwordEncryptor;
        this.expiresDuration = expiresDuration;
        this.base64 = new Base64(-1, null, true);
        this.secret = secret.getBytes(UTF_8);
    }

    @TransactionalRead
    public Login login(String username, String password) {
        try {
            UserSecret userSecret = userRepository.getUser(username);
            if (userSecret.user.role.perpetualToken) {
                throw new ValidationException(new Violation("LoginNotAllowed"));
            }
            if (!passwordEncryptor.checkPassword(password, userSecret.password)) {
                System.out.println("auth service: password check failed for user " + userSecret.user);
                throw new ValidationException(new Violation("BadCredentials"));
            }
            Login login = new Login();
            login.token = token(userSecret.user);
            login.username = userSecret.user.username;
            login.role = userSecret.user.role;
            return login;
        } catch (NotFoundException e) {
            System.out.println("auth service: user not found " + username);
            throw new ValidationException(new Violation("BadCredentials"));
        }
    }

    @TransactionalWrite
    public String resetToken(long userId) {
        UserSecret userSecret = userRepository.getUser(userId);
        if (!userSecret.user.role.perpetualToken) {
            throw new ValidationException(new Violation("PerpetualTokenNotAllowed"));
        }
        DateTime now = userRepository.getCurrentTime();
        userRepository.revokeTokens(userId, now);
        return token(userSecret.user, now);
    }

    private UserSecret loadUser(long id) {
        try {
            return userRepository.getUser(id);
        } catch (NotFoundException e) {
            throw new AuthenticationRequiredException();
        }
    }

    public String token(User user) {
        return token(user, now());
    }

    public String token(User user, DateTime now) {
        StringBuilder token = new StringBuilder()
                .append(user.role.perpetualToken ? "P" : "T").append(DELIM)
                .append(user.id).append(DELIM)
                .append(now.getMillis()).append(DELIM);

        token.append(hmac(token.toString()));

        return token.toString();
    }

    public String encryptPassword(String plain) {
        return passwordEncryptor.encryptPassword(plain);
    }

    private String hmac(String message) {
        byte[] mac = hmacHolder.get().doFinal(message.getBytes(UTF_8));
        return base64.encodeToString(mac);
    }

    @TransactionalRead
    public User authenticate(String token) {
        if (token == null) {
            throw new AuthenticationRequiredException();
        }
        Matcher m = TOKEN_PATTERN.matcher(token);
        if (!m.matches()) {
            throw new AuthenticationRequiredException();
        }
        String message = m.group("message");
        String type = m.group("type");
        long userId = Long.valueOf(m.group("userId"));
        long tokenTimestamp = Long.valueOf(m.group("timestamp"));
        String givenMac = m.group("hmac");
        String expectedMac = hmac(message);

        if (!expectedMac.equals(givenMac)) {
            throw new AuthenticationRequiredException();
        }
        boolean perpetualToken = false;
        switch (type) {
            case "T":
                // Temporal token expired?
                DateTime expires = new DateTime(tokenTimestamp).plus(expiresDuration);
                if (expires.isBeforeNow()) {
                    throw new AuthenticationRequiredException();
                }
                break;
            case "P":
                perpetualToken = true;
                break;
            default: new AuthenticationRequiredException();
        }

        UserSecret userSecret = loadUser(userId);

        // Token revoked?
        if (tokenTimestamp < userSecret.minTokenTimestamp.getMillis()) {
            throw new AuthenticationRequiredException();
        }

        // Token type mismatch
        if (userSecret.user.role.perpetualToken != perpetualToken) {
            throw new AuthenticationRequiredException();
        }

        return userSecret.user;
    }

}
