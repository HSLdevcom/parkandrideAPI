// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import java.util.Set;

import com.google.common.collect.Sets;

public class Login {

    public String token;

    public String username;

    public Role role;

    public Set<Permission> permissions = Sets.newHashSet();

    public Long operatorId;

    public Long userId;

    public Integer passwordExpireInDays;
}
