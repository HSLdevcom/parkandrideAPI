package fi.hsl.parkandride.inbound;

import java.util.Set;

import fi.hsl.parkandride.core.domain.SpatialSearch;

public class SpatialSearchDto {

    protected BBox bbox;

    protected Set<Long> ids;

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

    public SpatialSearch toSpatialSearch() {
        SpatialSearch search = new SpatialSearch();
        search.intersecting = bbox != null ? bbox.toPolygon() : null;
        search.ids = ids;
        return search;
    }

}
