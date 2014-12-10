package fi.hsl.parkandride.front;

import static fi.hsl.parkandride.front.UrlSchema.LOGIN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.hsl.parkandride.core.domain.Login;
import fi.hsl.parkandride.core.service.AuthenticationService;

@RestController
public class UserController {

    @Resource
    AuthenticationService authenticationService;

    @RequestMapping(value = LOGIN, method = POST, produces = APPLICATION_JSON_VALUE)
    public Login login(@RequestBody Credentials credentials) {
        return authenticationService.login(credentials.username, credentials.password);
    }

}
