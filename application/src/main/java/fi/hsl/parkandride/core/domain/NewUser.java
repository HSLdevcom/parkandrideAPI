package fi.hsl.parkandride.core.domain;

public class NewUser extends User {

    public String password;

    public NewUser() {}

    public NewUser(Long id, String username, Role role, String password) {
        super(id, username, role);
        this.password = password;
    }
}
