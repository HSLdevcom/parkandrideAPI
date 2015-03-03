package fi.hsl.parkandride.core.domain;

import java.util.Set;

public class ContactSearch {

    public int limit = 100;

    public long offset = 0l;

    public Sort sort;

    public Set<Long> ids;

    public MultilingualString name;

    public Long operatorId;

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

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

    public MultilingualString getName() {
        return name;
    }

    public void setName(MultilingualString name) {
        this.name = name;
    }

    public Long getOperatorId() {
        return operatorId;
    }
}
