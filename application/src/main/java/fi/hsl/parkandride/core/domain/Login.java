package fi.hsl.parkandride.core.domain;

import java.util.Set;

public class Login {

    public String token;

    public String username;

    public Role role;

    public Set<Permission> permissions;

    public Long operatorId;

}
