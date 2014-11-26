package fi.hsl.parkandride.core.domain;

public class User {

    public long id;

    public String username;

    public String password;

    public Role role;

    public User() {}

    public User(long id, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
