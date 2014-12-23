package fi.hsl.parkandride.core.domain;

public class NewUser extends User {

    public String password;


    @Override
    public String toString() {
        return "NewUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", operatorId=" + operatorId +
                '}';
    }
}
