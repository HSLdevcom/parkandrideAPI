package fi.hsl.parkandride.core.domain;

import java.util.Set;

import com.google.common.collect.Sets;

public class Login {

    public String token;

    public String username;

    public Role role;

    public Set<Permission> permissions = Sets.newHashSet();

    public Long operatorId;

}
