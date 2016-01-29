// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import org.geolatte.geom.Geometry;

import java.util.Set;

public class HubSearch {

    // NOTE: getters'n'setters are required for Spring GET request binding

    private Set<Long> ids;

    private Geometry geometry;

    private Double maxDistance;

    private Set<Long> facilityIds;

    private Sort sort;

    private int limit = -1;

    private long offset = 0;


    public Set<Long> getIds() {
        return ids;
    }

    public void setIds(Set<Long> ids) {
        this.ids = ids;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

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

    public Set<Long> getFacilityIds() {
        return facilityIds;
    }

    public void setFacilityIds(Set<Long> facilityIds) {
        this.facilityIds = facilityIds;
    }

    public Double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(Double maxDistance) {
        this.maxDistance = maxDistance;
    }

}
