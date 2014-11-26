package fi.hsl.parkandride.front;

import java.util.Base64;

import javax.annotation.Resource;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;

import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.service.AccessDeniedException;
import fi.hsl.parkandride.core.service.AuthenticationRequiredException;
import fi.hsl.parkandride.core.service.UserService;

public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String BASIC_PREFIX = "Basic ";

    @Resource UserService userService;

    private final Base64.Decoder base64 = Base64.getDecoder();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return User.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String authorization = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (Strings.isNullOrEmpty(authorization)) {
            throw new AuthenticationRequiredException();
        }
        if (!authorization.startsWith(BASIC_PREFIX)) {
            throw new AuthenticationRequiredException();
        }
        String credentials = new String(base64.decode(authorization.substring(BASIC_PREFIX.length())), Charsets.ISO_8859_1);
        int i = credentials.indexOf(':');
        if (Strings.isNullOrEmpty(credentials) || i < 1) {
            throw new AuthenticationRequiredException();
        }
        return userService.authenticate(credentials.substring(0, i), credentials.substring(i+1));
    }

}
