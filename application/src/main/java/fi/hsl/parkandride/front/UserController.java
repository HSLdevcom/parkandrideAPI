// Copyright © 2015 HSL

package fi.hsl.parkandride.front;

import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.AuthenticationService;
import fi.hsl.parkandride.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;

import static fi.hsl.parkandride.front.UrlSchema.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @Resource
    AuthenticationService authenticationService;

    @Resource
    UserService userService;

    @RequestMapping(method = POST, value = LOGIN, produces = APPLICATION_JSON_VALUE)
    public Login login(@RequestBody Credentials credentials) {
        log.info("login({})", urlEncode(credentials.getUsername()));
        return authenticationService.login(credentials.username, credentials.password);
    }

    @RequestMapping(method = GET, value = USERS, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchResults<User>> findUsers(User actor) {
        log.info("findUsers");
        UserSearch search = new UserSearch();
        search.setOperatorId(actor.operatorId);
        SearchResults<User> results = userService.findUsers(search, actor);
        return new ResponseEntity<>(results, OK);
    }

    @RequestMapping(method = POST, value = USERS, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createUser(@RequestBody NewUser newUser,
                                           User actor,
                                           UriComponentsBuilder builder) {
        log.info("createUser({}, {}, {})", newUser.role, urlEncode(newUser.username), newUser.operatorId);
        User createdUser = userService.createUser(newUser, actor);
        log.info("createUser({})", createdUser.id);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path(USER).buildAndExpand(createdUser.id).toUri());
        return new ResponseEntity<>(createdUser, headers, CREATED);
    }

    @RequestMapping(method = PUT, value = TOKEN, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ValueHolder<String>> resetToken(@PathVariable(USER_ID) long userId, User actor) {
        log.info("resetToken({})", userId);
        String token = userService.resetToken(userId, actor);
        return new ResponseEntity<>(ValueHolder.of(token), OK);
    }

    @RequestMapping(method = PUT, value = PASSWORD, produces = APPLICATION_JSON_VALUE)
    public void updatePassword(
            @PathVariable(USER_ID) long userId,
            @RequestBody ValueHolder<String> newPassword,
            User actor) {
        log.info("updatePassword({})", userId);
        userService.updatePassword(userId, newPassword.value, actor);
    }

    @RequestMapping(method = DELETE, value = USER, produces = APPLICATION_JSON_VALUE)
    public void deleteUser(@PathVariable(USER_ID) long userId, User actor) {
        log.info("deleteUser({})", userId);
        userService.deleteUser(userId, actor);
    }
}
