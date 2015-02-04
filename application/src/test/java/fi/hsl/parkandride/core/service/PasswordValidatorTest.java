package fi.hsl.parkandride.core.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PasswordValidatorTest {
    private final String password;
    private final boolean isExpectedToBeValid;

    public PasswordValidatorTest(String password, boolean isExpectedToBeValid) {
        this.password = password;
        this.isExpectedToBeValid = isExpectedToBeValid;
    }

    @Parameters
    public static Object[][] data() {
        return new Object[][]{
                { "i2E45678", true },          // min length
                { "i2E456789012345", true },   // max length
                { "i2E%&$)[^@#$%_-", true },   // special chars

                { null, false},
                { "12E45678", false },         // missing lowercase letter
                { "å2E45678", false },         // missing lowercase letter (å not considered as letter)
                { "i2345678", false },         // missing uppercase letter
                { "i2Ä45678", false },         // missing uppercase letter (Ä not considered as letter)
                { "i2E4 5678", false },        // whitespace inside
                { "i2E4567", false },          // too short
                { "i2E4567890123456", false }, // too long
        };
    }

    @Test
    public void testPassword() {
        assertIsValid(
                () -> PasswordValidator.validate(password),
                isExpectedToBeValid,
                password + " was " + (!isExpectedToBeValid ? "valid" : "invalid")
        );
    }

    private static void assertIsValid(Runnable r, boolean isValid, String message) {
        try {
            r.run();
            if (!isValid) {
                Assert.fail(message);
            }
        } catch (ValidationException e) {
            if (isValid) {
                Assert.fail(message);
            }
        }
    }
}