package fi.hsl.parkandride.inbound;

import java.util.Set;

import fi.hsl.parkandride.core.domain.PageableSpatialSearch;
import fi.hsl.parkandride.core.domain.SpatialSearch;

public class PageableSpatialSearchDto extends SpatialSearchDto {

    protected int limit = 100;

    protected long offset = 0l;

    protected Set<Long> ids;

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

    public Set<Long> getIds() {
        return ids;
    }

    public void setIds(Set<Long> ids) {
        this.ids = ids;
    }

    @Override
    public PageableSpatialSearch toSpatialSearch() {
        PageableSpatialSearch search = new PageableSpatialSearch();
        search.limit = limit;
        search.offset = offset;
        search.intersecting = bbox != null ? bbox.toPolygon() : null;
        search.ids = ids;
        return search;
    }
}
