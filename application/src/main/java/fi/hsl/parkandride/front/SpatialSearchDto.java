package fi.hsl.parkandride.front;

import java.util.Set;

import fi.hsl.parkandride.core.domain.Sort;
import fi.hsl.parkandride.core.domain.SpatialSearch;

public class SpatialSearchDto {

    protected BBox bbox;

    protected Set<Long> ids;

    protected Sort sort;

    public BBox getBbox() {
        return bbox;
    }

    public void setBbox(BBox bbox) {
        this.bbox = bbox;
    }

    public Set<Long> getIds() {
        return ids;
    }

    public void setIds(Set<Long> ids) {
        this.ids = ids;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public SpatialSearch toSpatialSearch() {
        return toSpatialSearch(new SpatialSearch());
    }

    protected <T extends SpatialSearch> T toSpatialSearch(T search) {
        search.intersecting = bbox != null ? bbox.toPolygon() : null;
        search.ids = ids;
        search.sort = sort;
        return search;
    }

}
