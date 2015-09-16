// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import javax.validation.Valid;

import org.joda.time.DateTime;

public class UserSecret {

    public String password;

    public DateTime passwordUpdatedTimestamp = new DateTime();

    public DateTime minTokenTimestamp = new DateTime();

    @Valid
    public User user;

    public UserSecret() {}

    public UserSecret(long id, String username, String password, Role role) {
        this.user = new User(id, username, role);
        this.password = password;
    }
}
