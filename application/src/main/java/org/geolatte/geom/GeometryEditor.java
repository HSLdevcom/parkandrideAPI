// Copyright © 2015 HSL

package org.geolatte.geom;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.beans.PropertyEditorSupport;

import fi.hsl.parkandride.core.domain.Spatial;

public class GeometryEditor extends PropertyEditorSupport {

    @Override
    public String getAsText() {
        Geometry value = (Geometry) getValue();
        if (value != null) {
            return Spatial.toWkt(value);
        }
        return null;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (!isNullOrEmpty(text)) {
            setValue(Spatial.fromWkt(text));
        } else {
            setValue(null);
        }
    }

}
