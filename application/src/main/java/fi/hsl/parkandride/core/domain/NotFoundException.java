package fi.hsl.parkandride.core.domain;

import javassist.compiler.NoFieldException;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String format, Object id) {
        super(String.format(format, id));
    }
}
