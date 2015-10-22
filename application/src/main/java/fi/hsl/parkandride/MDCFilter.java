// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride;

import fi.hsl.parkandride.front.IllegalHeaderException;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

import static java.lang.String.format;

@Component
public class MDCFilter extends GenericFilterBean {

    public static final String LIIPI_APPLICATION_ID = "Liipi-Application-Id";

    public static final Pattern APP_ID_PATTERN = Pattern.compile("[a-zA-Z0-9_\\-\\./]{3,20}");

    public interface Key {
        String REQUESTID = "requestid";
        String USERNAME = "username";
        String SRCIP = "srcip";
        String APPID = "appid";
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        setValues(request);
        try {
            chain.doFilter(request, response);
        } finally {
            unsetValues();
        }
    }

    private static void setValues(ServletRequest request) {
        HttpServletRequest httpReq = HttpServletRequest.class.cast(request);
        MDC.put(Key.REQUESTID, UUID.randomUUID().toString());
        MDC.put(Key.SRCIP, httpReq.getRemoteHost());
        // Authenticated username will be set in UserArgumentResolver iff authentication is required
        MDC.put(Key.USERNAME, "<ANONYMOUS>");
        String appId = httpReq.getHeader(LIIPI_APPLICATION_ID);
        if (appId != null) {
            MDC.put(Key.APPID, appId);
        }
    }

    public static void validateAppId(String appId) {
        if (!APP_ID_PATTERN.matcher(appId).matches()) {
            throw new IllegalHeaderException(
                    format("Illegal %s header. Value should match pattern %s.", LIIPI_APPLICATION_ID, APP_ID_PATTERN));
        }
    }

    private static  void unsetValues() {
        MDC.remove(Key.REQUESTID);
        MDC.remove(Key.USERNAME);
        MDC.remove(Key.SRCIP);
        MDC.remove(Key.APPID);
    }
}
