// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String format, Object id) {
        super(String.format(format, id));
    }
}
