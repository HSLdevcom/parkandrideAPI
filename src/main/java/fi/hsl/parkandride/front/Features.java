// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.front;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("feature")
public class Features {

    private boolean dev;

    private boolean mapNoTiles;

    public boolean isDev() {
        return dev;
    }

    public void setDev(boolean dev) {
        this.dev = dev;
    }

    public boolean isMapNoTiles() {
        return mapNoTiles;
    }

    public void setMapNoTiles(boolean mapNoTiles) {
        this.mapNoTiles = mapNoTiles;
    }
}
