// Copyright © 2018 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.domain.Violation;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

public class PasswordValidator {
    private PasswordValidator() {}

    private static final Pattern PATTERN = Pattern.compile(
            "^" +               // start
            "(?=.*\\d)" +       // at least one digit
            "(?=.*[a-zåäö])" +  // at least one lowercase letter
            "(?=.*[A-ZÅÄÖ])" +  // at least one uppercase letter
            "(?!.*\\s)" +       // no whitespace inside
            "." +               // everything provided that the previous condition checks pass
            "{8,50}" +          // min max length
            "$"                 // end
    );

    public static void validate(String password) {
        Collection<Violation> violations = new ArrayList<>();
        validate(password, violations);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }
    }

    public static void validate(String password, Collection<Violation> violations) {
        if (!StringUtils.hasText(password) || !PATTERN.matcher(password).matches()) {
            violations.add(new Violation("BadPassword", "password",
                    "Password with length of 8-50 must contain at least one digit, one lowercase and one uppercase character"));
        }
    }
}
