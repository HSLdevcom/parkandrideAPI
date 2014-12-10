package fi.hsl.parkandride.core.domain;

import javax.validation.constraints.NotNull;

public class User {

    public Long id;

    public String username;

    @NotNull
    public Role role;

    public Long operatorId;

    public User() {}

    public User(Long id, String username, Role role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
}
