package fi.hsl.parkandride.core.domain;

public class UserSearch implements OperatorEntity {

    public int limit = 100;

    public long offset = 0l;

    public Long operatorId;

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

    public long getOperatorId() {
        return operatorId;
    }

    @Override
    public Long operatorId() {
        return operatorId;
    }
}
