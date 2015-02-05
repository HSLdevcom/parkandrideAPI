package fi.hsl.parkandride.core.domain;

import static com.google.common.collect.Maps.filterKeys;
import static fi.hsl.parkandride.core.domain.PropertyPathTranslator.translate;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.validation.ConstraintViolation;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public class Violation {

    private static final Set<String> EXCLUDED_ARGUMENTS = ImmutableSet.of("message", "groups", "payload");

    public final String type;

    public final Map<String, Object> args;

    public final String path;

    public final String message;

    public Violation(String type) {
        this(type, ImmutableMap.of(), "", type);
    }

    public Violation(String type, String path, String message) {
        this(type, ImmutableMap.of(), path, message);
    }

    public Violation(String type, Map<String, Object> args, String path, String message) {
        this.type = type;
        this.args = args;
        this.path = path;
        this.message = message;
    }

    public Violation(ConstraintViolation cv) {
        this(getType(cv), getArgs(cv), getPath(cv), cv.getMessage());
    }

    private static Map<String, Object> getArgs(ConstraintViolation<?> cv) {
        // NOTE: This supports only simple types, not annotation parameters!
        return filterKeys(cv.getConstraintDescriptor().getAttributes(), new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String input) {
                return !EXCLUDED_ARGUMENTS.contains(input);
            }
        });
    }

    private static String getPath(ConstraintViolation cv) {
        return translate(cv.getPropertyPath().toString());
    }

    private static String getType(ConstraintViolation constraintViolation) {
        return constraintViolation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
    }

}
