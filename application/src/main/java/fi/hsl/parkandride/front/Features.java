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
