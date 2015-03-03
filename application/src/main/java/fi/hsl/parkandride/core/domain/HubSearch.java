package fi.hsl.parkandride.core.domain;

import java.util.Set;

import org.geolatte.geom.Geometry;

public class HubSearch {

    public Set<Long> ids;

    public Geometry geometry;

    public Double maxDistance;

    public Set<Long> facilityIds;

    public Sort sort;

    public int limit = 100;

    public long offset = 0l;

    // NOTE: getters'n'setters are required for Spring GET request binding

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
