// Copyright © 2018 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.front;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.ImmutableList;
import fi.hsl.parkandride.MDCFilter;
import fi.hsl.parkandride.core.domain.NotFoundException;
import fi.hsl.parkandride.core.domain.Violation;
import fi.hsl.parkandride.core.service.AccessDeniedException;
import fi.hsl.parkandride.core.service.AuthenticationRequiredException;
import fi.hsl.parkandride.core.service.ValidationException;
import org.apache.catalina.connector.ClientAbortException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static fi.hsl.parkandride.MDCFilter.LIIPI_APPLICATION_ID;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ControllerAdvice
public class ExceptionHandlers {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlers.class);

    @InitBinder
    public void validateApplicationId(HttpServletRequest request) {
        final String appId = request.getHeader(LIIPI_APPLICATION_ID);
        Optional.ofNullable(appId).ifPresent(MDCFilter::validateAppId);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = NOT_FOUND)
    public void notFound(HttpServletRequest req, NotFoundException ex) {
        // status: 404
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> validationException(HttpServletRequest request, ValidationException ex) {
        return handleError(request, BAD_REQUEST, ex, ex.getMessage(), ex.violations);
    }

    @ExceptionHandler(IllegalHeaderException.class)
    public ResponseEntity<Map<String, Object>> validationException(HttpServletRequest request, IllegalHeaderException ex) {
        return handleError(request, BAD_REQUEST, ex, ex.getMessage(), null);
    }

    @ExceptionHandler(ClientAbortException.class)
    public void clientAbortException(ClientAbortException e) {
        // Nothing to respond here as client has terminated connection
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> bindException(HttpServletRequest request, BindException ex) {
        List<Violation> violations = ex.getFieldErrors().stream()
                .map(fieldError -> new Violation(fieldError.getCode(), fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(toList());
        return handleError(request, BAD_REQUEST, ex, "Invalid request parameters", violations);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonException(HttpServletRequest request, HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof JsonMappingException) {
            JsonMappingException jsonEx = (JsonMappingException) ex.getCause();
            String path = getPath(jsonEx);
            Violation violation = new Violation("TypeMismatch", path, jsonEx.getMessage());
            return handleError(request, BAD_REQUEST, ex, "Invalid input", ImmutableList.of(violation));
        }
        return handleError(request, BAD_REQUEST, ex);
    }

    @ExceptionHandler(AuthenticationRequiredException.class)
    @ResponseBody
    public ResponseEntity<Void> authenticationRequiredException(HttpServletRequest request, AuthenticationRequiredException ex) {
        log.info("Authentication failed for request " + request.getMethod() + " " + request.getRequestURI(), ex);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<Void>(null, headers, UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<Void> accessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<Void>((Void) null, FORBIDDEN);
    }

    private String getPath(JsonMappingException jsonEx) {
        StringBuilder path = new StringBuilder();
        for (JsonMappingException.Reference ref : jsonEx.getPath()) {
            String field = ref.getFieldName();
            int index = ref.getIndex();
            if (field != null) {
                if (path.length() > 0) {
                    path.append('.');
                }
                path.append(field);
            }
            if (index >= 0) {
                path.append('[').append(index).append(']');
            }
        }
        return path.toString();
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class, HttpMediaTypeException.class})
    public ResponseEntity<Map<String, Object>> methodNotSupportedException(HttpServletRequest request, ServletException ex) {
        return handleError(request, BAD_REQUEST, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> exception(HttpServletRequest request, Exception ex) {
        return handleError(request, INTERNAL_SERVER_ERROR, ex);
    }

    private ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request, HttpStatus status, Throwable ex) {
        return handleError(request, status, ex, ex.getMessage(), null);
    }

    private ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request, HttpStatus status, Throwable ex,
                                                            String message, List<Violation> violations) {
        ex = resolveError(ex);
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        errorAttributes.put("status", status.value());
        errorAttributes.put("message", message);
        errorAttributes.put("timestamp", DateTime.now());
        errorAttributes.put("exception", resolveError(ex).getClass().getName());
        if (violations != null && !violations.isEmpty()) {
            errorAttributes.put("violations", violations);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        return new ResponseEntity<>(errorAttributes, headers, status);
    }

    private Throwable resolveError(Throwable ex) {
        while (ex instanceof ServletException && ex.getCause() != null) {
            ex = ((ServletException) ex).getCause();
        }
        return ex;
    }
}
