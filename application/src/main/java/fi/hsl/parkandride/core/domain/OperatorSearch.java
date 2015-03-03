package fi.hsl.parkandride.core.domain;

public class OperatorSearch {

    public int limit = 100;

    public long offset = 0l;

    public Sort sort;

    // NOTE: getters'n'setters are required for Spring GET request binding

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

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

}
