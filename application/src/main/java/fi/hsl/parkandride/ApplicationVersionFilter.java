// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride;

import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ApplicationVersionFilter implements Filter {

    private final String version;

    public ApplicationVersionFilter(String version) {
        this.version = version;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        if (StringUtils.hasText(version)) {
            response.setHeader("Liipi-Version", version);
        }
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }
}
