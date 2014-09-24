package fi.hsl.parkandride.inbound;

import fi.hsl.parkandride.core.domain.PageableSpatialSearch;
import fi.hsl.parkandride.core.domain.SpatialSearch;

public class SpatialSearchDto {

    protected BBox bbox;

    public BBox getBbox() {
        return bbox;
    }

    public void setBbox(BBox bbox) {
        this.bbox = bbox;
    }

    public SpatialSearch toSpatialSearch() {
        SpatialSearch search = new SpatialSearch();
        search.intersecting = bbox != null ? bbox.toPolygon() : null;
        return search;
    }

}
