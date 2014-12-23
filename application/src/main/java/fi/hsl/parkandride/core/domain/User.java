package fi.hsl.parkandride.core.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

public class User {

    public Long id;

    @NotBlank
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", operatorId=" + operatorId +
                '}';
    }
}
