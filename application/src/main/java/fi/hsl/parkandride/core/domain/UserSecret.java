// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import javax.validation.Valid;

import org.joda.time.DateTime;

public class UserSecret {

    public String password;

    public DateTime minTokenTimestamp = new DateTime();

    @Valid
    public User user;

    public UserSecret() {}

    public UserSecret(long id, String username, String password, Role role) {
        this.user = new User(id, username, role);
        this.password = password;
    }
}
