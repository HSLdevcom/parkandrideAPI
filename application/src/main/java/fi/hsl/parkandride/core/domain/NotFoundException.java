// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String format, Object id) {
        super(String.format(format, id));
    }
}
