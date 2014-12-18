package fi.hsl.parkandride.core.domain;

import static fi.hsl.parkandride.core.domain.Permission.FACILITY_STATUS_UPDATE;
import static fi.hsl.parkandride.core.domain.Permission.HUB_CREATE;
import static fi.hsl.parkandride.core.domain.Permission.HUB_UPDATE;
import static fi.hsl.parkandride.core.domain.Permission.OPERATOR_CREATE;
import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

public enum Role {
    ADMIN(false, exclude(FACILITY_STATUS_UPDATE)),

    OPERATOR(false, exclude(FACILITY_STATUS_UPDATE, OPERATOR_CREATE, HUB_CREATE, HUB_UPDATE)),

    OPERATOR_API(true, FACILITY_STATUS_UPDATE);

    public final boolean perpetualToken;

    public final Set<Permission> permissions;

    Role(boolean perpetualToken, Permission... permissions) {
        this.perpetualToken = perpetualToken;
        this.permissions = ImmutableSet.copyOf(permissions);
    }

    private static Permission[] exclude(Permission... excludedPermissions) {
        Set<Permission> permissions = new HashSet<>(asList(Permission.values()));
        for (Permission excluded : excludedPermissions) {
            permissions.remove(excluded);
        }
        return permissions.toArray(new Permission[permissions.size()]);
    }
}
