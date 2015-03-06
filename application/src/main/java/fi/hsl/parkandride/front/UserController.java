package fi.hsl.parkandride.front;

import static fi.hsl.parkandride.front.UrlSchema.*;
import static java.lang.String.format;
import static java.net.URLEncoder.encode;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.net.URLEncoder;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @Resource
    AuthenticationService authenticationService;

    @Resource
    UserService userService;

    @RequestMapping(method = POST, value = LOGIN, produces = APPLICATION_JSON_VALUE)
    public Login login(@RequestBody Credentials credentials) {
        log.info(format("login(%s)", urlEncode(credentials.getUsername())));
        return authenticationService.login(credentials.username, credentials.password);
    }

    @RequestMapping(method = GET, value = USERS, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchResults<User>> findUsers(User actor) {
        log.info("findUsers");
        UserSearch search = new UserSearch();
        search.operatorId = actor.operatorId;
        SearchResults<User> results = userService.findUsers(search, actor);
        return new ResponseEntity<>(results, OK);
    }

    @RequestMapping(method = POST, value = USERS, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createUser(@RequestBody NewUser newUser,
                                                   User actor,
                                                   UriComponentsBuilder builder) {
        log.info(format("createUser(%s, %s, %s)", newUser.role, urlEncode(newUser.username), newUser.operatorId));
        User createdUser = userService.createUser(newUser, actor);
        log.info(format("createUser(%s)", createdUser.id));

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path(USER).buildAndExpand(createdUser.id).toUri());
        return new ResponseEntity<>(createdUser, headers, CREATED);
    }

    @RequestMapping(method = PUT, value = TOKEN, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ValueHolder<String>> resetToken(@PathVariable(USER_ID) long userId, User actor) {
        log.info(format("resetToken(%s)", userId));
        String token = userService.resetToken(userId, actor);
        return new ResponseEntity<>(ValueHolder.of(token), OK);
    }

    @RequestMapping(method = PUT, value = PASSWORD, produces = APPLICATION_JSON_VALUE)
    public void updatePassword(
            @PathVariable(USER_ID) long userId,
            @RequestBody ValueHolder<String> newPassword,
            User actor) {
        log.info("updatePassword(%s)", userId);
        userService.updatePassword(userId, newPassword.value, actor);
    }

    @RequestMapping(method = DELETE, value = USER, produces = APPLICATION_JSON_VALUE)
    public void deleteUser(@PathVariable(USER_ID) long userId, User actor) {
        log.info("deleteUser(%s)", userId);
        userService.deleteUser(userId, actor);
    }
}
