package fi.hsl.parkandride.core.domain;

import static fi.hsl.parkandride.core.domain.Permission.ALL_OPERATORS;
import static fi.hsl.parkandride.core.domain.Permission.FACILITY_UTILIZATION_UPDATE;
import static fi.hsl.parkandride.core.domain.Permission.HUB_CREATE;
import static fi.hsl.parkandride.core.domain.Permission.HUB_UPDATE;
import static fi.hsl.parkandride.core.domain.Permission.OPERATOR_CREATE;
import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

public enum Role {
    ADMIN(false, exclude(FACILITY_UTILIZATION_UPDATE)),

    OPERATOR(false, exclude(ALL_OPERATORS, FACILITY_UTILIZATION_UPDATE, OPERATOR_CREATE, HUB_CREATE, HUB_UPDATE)),

    OPERATOR_API(true, include(FACILITY_UTILIZATION_UPDATE));

    public final boolean perpetualToken;

    public final Set<Permission> permissions;

    Role(boolean perpetualToken, ImmutableSet<Permission> permissions) {
        this.perpetualToken = perpetualToken;
        this.permissions = permissions;
    }

    private static ImmutableSet<Permission> exclude(Permission... excludedPermissions) {
        Set<Permission> permissions = new HashSet<>(asList(Permission.values()));
        for (Permission excluded : excludedPermissions) {
            permissions.remove(excluded);
        }
        return ImmutableSet.copyOf(permissions);
    }

    private static ImmutableSet<Permission> include(Permission... includedPermissions) {
        return ImmutableSet.copyOf(includedPermissions);
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
}
