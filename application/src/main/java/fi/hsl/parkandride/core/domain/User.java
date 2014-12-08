package fi.hsl.parkandride.core.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

public class User {

    public long id;

    public String username;

    public String password;

    @NotBlank
    public String secret;

    @NotNull
    public Role role;

    public User() {}

    public User(long id, String username, String password, Role role, String secret) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.secret = secret;
    }
}
