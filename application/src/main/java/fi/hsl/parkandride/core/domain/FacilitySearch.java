package fi.hsl.parkandride.core.domain;

import org.geolatte.geom.Polygon;

public class FacilitySearch {

    public int limit = 100;

    public long offset = 0l;

    public Polygon intersecting;

    // Getters and setters for Spring data binder

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public Polygon getIntersecting() {
        return intersecting;
    }

    public void setIntersecting(Polygon intersecting) {
        this.intersecting = intersecting;
    }
}
