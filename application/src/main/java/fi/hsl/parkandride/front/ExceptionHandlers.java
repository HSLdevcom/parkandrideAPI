package fi.hsl.parkandride.front;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.ImmutableList;
import static com.google.common.net.HttpHeaders.*;
import org.springframework.http.HttpHeaders;

import fi.hsl.parkandride.core.domain.NotFoundException;
import fi.hsl.parkandride.core.domain.Violation;
import fi.hsl.parkandride.core.service.AccessDeniedException;
import fi.hsl.parkandride.core.service.AuthenticationRequiredException;
import fi.hsl.parkandride.core.service.ValidationException;

@ControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value= NOT_FOUND)
    public void notFound(HttpServletRequest req, NotFoundException ex) {

    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> validationException(HttpServletRequest request, ValidationException ex) {
        return handleError(request, BAD_REQUEST, ex, ex.getMessage(), ex.violations);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> bindException(HttpServletRequest request, BindException ex) {
        List<Violation> violations = new ArrayList<>();
        for (FieldError fieldError : ex.getFieldErrors()) {
            violations.add(new Violation(fieldError.getCode(), fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return handleError(request, BAD_REQUEST, ex, "Invalid request parameters", violations);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonException(HttpServletRequest request, HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof JsonMappingException) {
            JsonMappingException jsonEx = (JsonMappingException) ex.getCause();
            String path = getPath(jsonEx);
            Violation violation = new Violation("TypeMismatch", path, jsonEx.getMessage());
            return handleError(request, BAD_REQUEST, ex, "Invalid JSON", ImmutableList.of(violation));
        }
        return handleError(request, BAD_REQUEST, ex);
    }

    @ExceptionHandler(AuthenticationRequiredException.class)
    @ResponseBody
    public ResponseEntity<Void> authenticationRequiredException(AuthenticationRequiredException ex) {
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
            if (path.length() > 0) {
                path.append('.');
            }
            String field = ref.getFieldName();
            int index = ref.getIndex();
            if (field != null) {
                path.append(field);
            }
            if (index >= 0) {
                path.append('[').append(index).append(']');
            }
        }
        return path.toString();
    }

    @ExceptionHandler({ HttpRequestMethodNotSupportedException.class, HttpMediaTypeException.class })
    public ResponseEntity<Map<String, Object>> methodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException ex) {
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
        return new ResponseEntity<>(errorAttributes, status);
    }

    private Throwable resolveError(Throwable ex) {
        while (ex instanceof ServletException && ex.getCause() != null) {
            ex = ((ServletException) ex).getCause();
        }
        return ex;
    }
}
