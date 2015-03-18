// Copyright © 2015 HSL

package fi.hsl.parkandride.front;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

import java.util.Base64;

import javax.annotation.Resource;

import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.google.common.base.Strings;

import fi.hsl.parkandride.MDCFilter;
import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.service.AuthenticationRequiredException;
import fi.hsl.parkandride.core.service.AuthenticationService;

public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String BEARER_PREFIX = "Bearer ";

    @Resource
    AuthenticationService authenticationService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return User.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String authorization = webRequest.getHeader(AUTHORIZATION);
        if (Strings.isNullOrEmpty(authorization)) {
            throw new AuthenticationRequiredException();
        }
        if (!authorization.startsWith(BEARER_PREFIX)) {
            throw new AuthenticationRequiredException();
        }
        String token = authorization.substring(BEARER_PREFIX.length()).trim();
        User user = authenticationService.authenticate(token);
        MDC.put(MDCFilter.Key.USERNAME, user.username);
        return user;
    }

}
