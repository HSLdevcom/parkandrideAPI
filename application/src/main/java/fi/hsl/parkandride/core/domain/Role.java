package fi.hsl.parkandride.core.domain;

public enum Role {
    ADMIN(false),
    OPERATOR(false),
    OPERATOR_API(true);

    public final boolean perpetualToken;

    Role(boolean perpetualToken) {
        this.perpetualToken = perpetualToken;
    }

}
