package fi.hsl.parkandride.core.service;

import static com.google.common.base.Charsets.UTF_8;
import static org.joda.time.DateTime.now;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.util.password.PasswordEncryptor;
import org.joda.time.DateTime;
import org.joda.time.Duration;

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
            "(" + // 1: message for hmac
            "(\\d+)" + DELIM_REGEX + // 2: userId
            "(\\d+)" + DELIM_REGEX + // 3: timestamp
            ")" + // (1: message for hmac)
            "([A-Za-z0-9\\-_]+)" +   // 4: base64url(hmac)
            "$" // end
    );

    private final PasswordEncryptor passwordEncryptor;

    private static final String HMAC = "HmacSHA1";

    private final Base64 base64;

    private final Duration expiresDuration;

    private volatile SecureRandom random;

    private AtomicLong randomCount = new AtomicLong(0);

    private final int reseedInterval = 100;

    // lengths divisible by 6 go nicely with base64
    private final int secretLength = 18;

    private UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository, PasswordEncryptor passwordEncryptor) {
        this.userRepository = userRepository;
        this.passwordEncryptor = passwordEncryptor;
        this.expiresDuration = Duration.standardHours(1);
        this.base64 = new Base64(-1, null, true);
        getSecureRandom();
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

    @TransactionalRead
    public Login login(String username, String password) {
        try {
            UserSecret userSecret = userRepository.getUser(username);
            if (userSecret.user.role.perpetualToken) {
                throw new RuntimeException("Login not allowed for API-user");
            }
            if (!passwordEncryptor.checkPassword(password, userSecret.password)) {
                throw new ValidationException(new Violation("BadCredentials"));
            }
            Login login = new Login();
            login.token = token(userSecret.secret, userSecret.user.id);
            login.username = userSecret.user.username;
            login.role = userSecret.user.role;
            return login;
        } catch (NotFoundException e) {
            throw new ValidationException(new Violation("BadCredentials"));
        }
    }



    private UserSecret loadUser(long id) {
        try {
            return userRepository.getUser(id);
        } catch (NotFoundException e) {
            throw new AuthenticationRequiredException();
        }
    }

    private String token(String secret, Long userId) {
        StringBuilder token = new StringBuilder();
        token.append(userId).append(DELIM);
        token.append(now().getMillis()).append(DELIM);
        token.append(hmac(secret, token.toString()));
        return token.toString();
    }

    public String newSecret() {
        return generateRandom(secretLength);
    }

    public String generateRandom(int length) {
        byte[] bytes = new byte[length];
        getSecureRandom().nextBytes(bytes);
        return base64.encodeAsString(bytes);
    }

    public String encryptPassword(String plain) {
        return passwordEncryptor.encryptPassword(plain);
    }

    private String resetSecret(UserSecret userSecret) {
        userSecret.secret = newSecret();
        userRepository.updateSecret(userSecret.user.id, userSecret.secret);
        return userSecret.secret;
    }

    private String hmac(String secret, String message) {
        try {
            Mac hmac = Mac.getInstance(HMAC);
            SecretKeySpec key = new SecretKeySpec(base64.decode(secret), HMAC);
            hmac.init(key);
            byte[] mac = hmac.doFinal(message.getBytes(UTF_8));
            return base64.encodeToString(mac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
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
        String message = m.group(1);
        long userId = Long.valueOf(m.group(2));
        long timestamp = Long.valueOf(m.group(3));
        String givenMac = m.group(4);

        UserSecret userSecret = loadUser(userId);
        String expectedMac = hmac(userSecret.secret, message);

        if (!userSecret.user.role.perpetualToken) {
            DateTime expires = new DateTime(timestamp).plus(expiresDuration);
            if (expires.isBeforeNow()) {
                throw new AuthenticationRequiredException();
            }
        }

        if (!expectedMac.equals(givenMac)) {
            throw new AuthenticationRequiredException();
        }

        return userSecret.user;
    }

}
