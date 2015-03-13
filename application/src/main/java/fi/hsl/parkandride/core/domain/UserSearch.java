package fi.hsl.parkandride.core.domain;

public class UserSearch implements OperatorEntity {

    // NOTE: getters'n'setters are required for Spring GET request binding

    private int limit = 100;

    private long offset = 0l;

    private Long operatorId;


    @Override
    public Long operatorId() {
        return operatorId;
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

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }
}
