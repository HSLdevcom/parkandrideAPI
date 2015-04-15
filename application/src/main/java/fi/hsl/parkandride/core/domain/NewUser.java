// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

public class NewUser extends User {

    public String password;

    public NewUser() {}

    public NewUser(Long id, String username, Role role, String password) {
        super(id, username, role);
        this.password = password;
    }

    public NewUser(Long id, String username, Role role, Long operatorId, String password) {
        super(id, username, role, operatorId);
        this.password = password;
    }
}
