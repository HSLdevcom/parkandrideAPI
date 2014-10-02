package fi.hsl.parkandride.core.domain;

import javax.validation.ConstraintViolation;
import javax.validation.Path;

public class Violation {

    public final String type;

    public final String path;

    public final String message;

    public Violation(String type) {
        this(type, null, type);
    }

    public Violation(String type, String path, String message) {
        this.type = type;
        this.path = path;
        this.message = message;
    }

    public Violation(ConstraintViolation cv) {
        this(getType(cv), getPath(cv), cv.getMessage());
    }

    private static String getPath(ConstraintViolation cv) {
        return cv.getPropertyPath().toString();
    }

    private static String getType(ConstraintViolation constraintViolation) {
        return constraintViolation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
    }

}
