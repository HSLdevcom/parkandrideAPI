// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import java.util.Set;

public class ContactSearch {

    // NOTE: getters'n'setters are required for Spring GET request binding

    private int limit = 100;

    private long offset = 0;

    private Sort sort;

    private Set<Long> ids;

    private Long operatorId;


    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
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

    public Long getOperatorId() {
        return operatorId;
    }
}
