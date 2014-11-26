package fi.hsl.parkandride;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class MdcFilter extends GenericFilterBean {
    public interface Key {
        String REQUESTID = "requestid";
        String USERNAME = "username";
        String SRCIP = "srcip";
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
        String username = httpReq.getRemoteUser();
        if (username != null) {
            MDC.put(Key.USERNAME, username);
        }
    }

    private static  void unsetValues() {
        MDC.remove(Key.REQUESTID);
        MDC.remove(Key.USERNAME);
        MDC.remove(Key.SRCIP);
    }
}
