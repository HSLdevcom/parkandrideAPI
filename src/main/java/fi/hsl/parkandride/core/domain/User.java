// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

public class User implements OperatorEntity {

    public Long id;

    @NotBlank
    public String username;

    @NotNull
    public Role role;

    public Long operatorId;

    public User() {}

    public User(Long id, String username, Role role) {
        this(id, username, role, null);
    }

    public User(Long id, String username, Role role, Long operatorId) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.operatorId = operatorId;
    }

    public User(User other) {
        this.username = other.username;
        this.role = other.role;
        this.operatorId = other.operatorId;
    }

    @Override
    public final Long operatorId() {
        return operatorId;
    }

    public final boolean hasPermission(Permission permission) {
        return role != null && role.hasPermission(permission);
    }
}
