// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.geolatte.geom.Geometry;
import javax.annotation.Nonnull;

public class Region {

    public long id;

    @Nonnull
    public MultilingualString name;

    @Nonnull
    @JsonIgnore
    public Geometry area;

    public static final Region UNKNOWN_REGION;

    static {
        UNKNOWN_REGION = new Region();
        UNKNOWN_REGION.id = -1;
        UNKNOWN_REGION.name = new MultilingualString("");
    }
}
