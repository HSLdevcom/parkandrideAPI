package fi.hsl.parkandride.inbound;

import fi.hsl.parkandride.core.domain.FacilitySearch;

public class FacilitySearchDto {

    private int limit = 100;

    private long offset = 0l;

    private BBox bbox;

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

    public BBox getBbox() {
        return bbox;
    }

    public void setBbox(BBox bbox) {
        this.bbox = bbox;
    }

    public FacilitySearch toFacilitySearch() {
        FacilitySearch search = new FacilitySearch();
        search.limit = limit;
        search.offset = offset;
        search.intersecting = bbox != null ? bbox.toPolygon() : null;
        return search;
    }
}
