// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

public enum Permission {
    ALL_OPERATORS(false),
    FACILITY_CREATE(true),
    FACILITY_UPDATE(true),
    OPERATOR_CREATE(false),
    OPERATOR_UPDATE(true),
    CONTACT_CREATE(true),
    CONTACT_UPDATE(true),
    USER_CREATE(true),
    USER_UPDATE(true),
    USER_VIEW(true),
    FACILITY_UTILIZATION_UPDATE(true),
    HUB_CREATE(false),
    HUB_UPDATE(false);

    public final boolean requiresContext;

    Permission(boolean requiresContext) {
        this.requiresContext = requiresContext;
    }
}
