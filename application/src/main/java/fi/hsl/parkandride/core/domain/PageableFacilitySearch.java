// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

public class PageableFacilitySearch extends FacilitySearch {

    public Sort sort;

    public int limit = 100;

    public long offset = 0l;

    // NOTE: getters'n'setters are required for Spring GET request binding

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
}
