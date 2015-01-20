package fi.hsl.parkandride.front;

import static fi.hsl.parkandride.front.UrlSchema.LOGIN;
import static fi.hsl.parkandride.front.UrlSchema.USERS;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.hsl.parkandride.core.domain.Login;
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
}
