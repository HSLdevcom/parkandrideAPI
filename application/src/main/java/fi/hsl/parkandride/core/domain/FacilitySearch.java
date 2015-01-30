package fi.hsl.parkandride.core.domain;

import java.util.Set;

import org.geolatte.geom.Geometry;

public class FacilitySearch {

    public Set<Long> ids;

    public Set<FacilityStatus> statuses;

    public Geometry geometry;

    public Set<Long> getIds() {
        return ids;
    }

    public void setIds(Set<Long> ids) {
        this.ids = ids;
    }

    public Set<FacilityStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(Set<FacilityStatus> statuses) {
        this.statuses = statuses;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
