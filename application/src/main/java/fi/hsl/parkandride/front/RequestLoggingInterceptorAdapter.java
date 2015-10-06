// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.front;

import fi.hsl.parkandride.core.domain.RequestLogKey;
import fi.hsl.parkandride.core.service.BatchingRequestLogService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class RequestLoggingInterceptorAdapter extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptorAdapter.class);

    // The source header for API requests
    public static final String X_HSL_SOURCE = "X-HSL-Source";
    public static final String UNKNOWN_SOURCE = "<Unknown>";

    private final BatchingRequestLogService batchingRequestLogService;

    public RequestLoggingInterceptorAdapter(BatchingRequestLogService batchingRequestLogService) {
        this.batchingRequestLogService = batchingRequestLogService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Optional.ofNullable((String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE))
                .filter(s -> s.startsWith(UrlSchema.API))
                .ifPresent(urlPattern -> {
                    final String source = Optional.ofNullable(request.getHeader(X_HSL_SOURCE)).orElse(UNKNOWN_SOURCE);
                    logger.trace("Intercepted API call: <{}> for source <{}>", urlPattern, source);
                    batchingRequestLogService.increment(new RequestLogKey(urlPattern, source, DateTime.now()));
                });
        return super.preHandle(request, response, handler);
    }
}
