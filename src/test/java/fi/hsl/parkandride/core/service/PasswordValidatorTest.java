// Copyright © 2018 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
                { "å2Ä%&$)[^@#$%_-", true },   // å,ä,ö + special chars

                { null, false},
                { "12E45678", false },         // missing lowercase letter
                { "i2345678", false },         // missing uppercase letter
                { "i2E4 5678", false },        // whitespace inside
                { "i2E4567", false },          // too short
                { "i2E4567890i2E4567890i2E4567890i2E4567890i2E4567890x", false }, // too long
        };
    }

    @Test
    public void testPassword() {
        try {
            PasswordValidator.validate(password);
            assertTrue("Invalid password '"+ password + "' was valid", isExpectedToBeValid);
        } catch (ValidationException e) {
            assertFalse("Valid password '" + password + "' was invalid", isExpectedToBeValid);
        }
    }
}