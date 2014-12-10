package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.domain.UserSearch;
import fi.hsl.parkandride.core.domain.UserSecret;

public interface UserRepository {

    long insertUser(UserSecret userSecret);

    void updateSecret(long userId, String secret);

    void updatePassword(long userId, String password, String secret);

    void updateUser(long userId, User user);

    UserSecret getUser(String username);

    UserSecret getUser(long userId);

    SearchResults<User> findUsers(UserSearch search);

}
