package fi.hsl.parkandride.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;

import org.assertj.core.groups.Tuple;

import fi.hsl.parkandride.core.domain.Violation;
import fi.hsl.parkandride.core.service.ValidationException;

public class ViolationAssert {
    public static List<Violation> violations(Runnable r) {
        try {
            r.run();
            throw new AssertionError("did not throw ValidationException");
        } catch (ValidationException e) {
            return e.violations;
        }
    }

    public static void assertOperatorRequired(Runnable r) {
        assertTypeAndPath(r, tuple("OperatorRequired", "operator"));
    }

    public static void assertBadPassword(Runnable r) {
        assertTypeAndPath(r, tuple("BadPassword", "password"));
    }

    public static void  assertOperatorNotAllowed(Runnable r) {
        assertTypeAndPath(r, tuple("OperatorNotAllowed", "operator"));
    }

    public static void assertNotNull(Runnable r) {
        assertTypeAndPath(r, tuple("NotNull", "role"));
    }

    public static void assertPerpetualTokenNotAllowed(Runnable r) {
        assertTypeAndPath(r, tuple("PerpetualTokenNotAllowed", ""));
    }

    public static void assertPasswordUpdateNotApplicable(Runnable r) {
        assertTypeAndPath(r, tuple("PasswordUpdateNotApplicable", ""));
    }

    public static void assertTypeAndPath(Runnable r, Tuple... tuples) {
        assertThat(violations(r)).extracting("type", "path").containsOnly(tuples);
    }
}
