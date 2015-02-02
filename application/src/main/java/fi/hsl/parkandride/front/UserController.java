package fi.hsl.parkandride.front;

import static fi.hsl.parkandride.front.UrlSchema.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import javax.annotation.Resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import fi.hsl.parkandride.core.domain.Login;
import fi.hsl.parkandride.core.domain.NewUser;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.domain.UserSearch;
import fi.hsl.parkandride.core.service.AuthenticationService;
import fi.hsl.parkandride.core.service.UserService;

@RestController
public class UserController {

    @Resource
    AuthenticationService authenticationService;

    @Resource
    UserService userService;

    @RequestMapping(method = POST, value = LOGIN, produces = APPLICATION_JSON_VALUE)
    public Login login(@RequestBody Credentials credentials) {
        return authenticationService.login(credentials.username, credentials.password);
    }

    @RequestMapping(method = GET, value = USERS, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchResults<User>> findUsers(User currentUser) {
        UserSearch search = new UserSearch();
        search.operatorId = currentUser.operatorId;
        SearchResults<User> results = userService.findUsers(search, currentUser);
        return new ResponseEntity<>(results, OK);
    }

    @RequestMapping(method = POST, value = USERS, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createUser(@RequestBody NewUser newUser,
                                                   User creator,
                                                   UriComponentsBuilder builder) {
        User createdUser = userService.createUser(newUser, creator);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path(USER).buildAndExpand(createdUser.id).toUri());
        return new ResponseEntity<>(createdUser, headers, CREATED);
    }

    @RequestMapping(method = PUT, value = TOKEN, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ValueHolder<String>> resetToken(@PathVariable(USER_ID) long userId, User updater) {
        String token = userService.resetToken(userId, updater);
        return new ResponseEntity<>(ValueHolder.of(token), OK);
    }

    @RequestMapping(method = PUT, value = PASSWORD, produces = APPLICATION_JSON_VALUE)
    public void updatePassword(
            @PathVariable(USER_ID) long userId,
            @RequestBody ValueHolder<String> newPassword,
            User updater) {
        userService.updatePassword(userId, newPassword.value, updater);
    }

    @RequestMapping(method = DELETE, value = USER, produces = APPLICATION_JSON_VALUE)
    public void deleteUser(@PathVariable(USER_ID) long userId, User remover) {
        userService.deleteUser(userId, remover);
    }
}
