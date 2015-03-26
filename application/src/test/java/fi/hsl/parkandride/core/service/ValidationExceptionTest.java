// Copyright © 2015 HSL

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.domain.Violation;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationExceptionTest {

    @Test
    public void exception_message_contains_all_violation_messages() {
        List<Violation> violations = Arrays.asList(
                new Violation("Type1", "path.one", ""),
                new Violation("Type2", "path.two", "")
        );

        assertThat(new ValidationException(violations).getMessage()).isEqualTo("Invalid data. Violations in path.one (Type1), path.two (Type2)");
    }
}
