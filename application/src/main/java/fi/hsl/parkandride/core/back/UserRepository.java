// Copyright © 2015 HSL

package fi.hsl.parkandride.core.back;

import org.joda.time.DateTime;

import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.domain.UserSearch;
import fi.hsl.parkandride.core.domain.UserSecret;

public interface UserRepository {

    long insertUser(UserSecret userSecret);

    DateTime getCurrentTime();

    void revokeTokens(long userId, DateTime asOf);

    void updatePassword(long userId, String password);

    void updateUser(long userId, User user);

    UserSecret getUser(String username);

    UserSecret getUser(long userId);

    SearchResults<User> findUsers(UserSearch search);

    void deleteUser(long userId);
}
