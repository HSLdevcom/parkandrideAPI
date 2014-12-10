package fi.hsl.parkandride.core.domain;

import org.hibernate.validator.constraints.NotBlank;

public class UserSecret {

    public String password;

    @NotBlank
    public String secret;

    public User user;

    public UserSecret() {}

    public UserSecret(long id, String username, String password, Role role, String secret) {
        this.user = new User(id, username, role);
        this.password = password;
        this.secret = secret;
    }
}
