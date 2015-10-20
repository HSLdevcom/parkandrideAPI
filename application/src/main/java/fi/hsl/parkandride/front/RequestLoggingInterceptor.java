// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.front;

import fi.hsl.parkandride.MDCFilter;
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

public class RequestLoggingInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    // The source header for API requests
    public static final String SOURCE_HEADER = MDCFilter.LIIPI_APPLICATION_ID;

    private final BatchingRequestLogService batchingRequestLogService;

    public RequestLoggingInterceptor(BatchingRequestLogService batchingRequestLogService) {
        this.batchingRequestLogService = batchingRequestLogService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Optional.ofNullable((String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE))
                .filter(s -> s.startsWith(UrlSchema.API))
                .ifPresent(urlPattern -> {
                    final String source = request.getHeader(SOURCE_HEADER);
                    logger.trace("Intercepted API call: <{}> for source <{}>", urlPattern, source);
                    batchingRequestLogService.increment(new RequestLogKey(urlPattern, source, DateTime.now()));
                });
        return super.preHandle(request, response, handler);
    }
}
