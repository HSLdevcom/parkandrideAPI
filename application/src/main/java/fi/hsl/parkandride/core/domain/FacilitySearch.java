// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import java.util.Set;

import org.geolatte.geom.Geometry;

public class FacilitySearch {

    // NOTE: getters'n'setters are required for Spring GET request binding

    private Set<Long> ids;

    private Set<FacilityStatus> statuses;

    private Double maxDistance;

    private Geometry geometry;

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

    public Double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(Double maxDistance) {
        this.maxDistance = maxDistance;
    }
}
