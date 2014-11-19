package fi.hsl.parkandride.front;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import fi.hsl.parkandride.core.domain.NotFoundException;
import fi.hsl.parkandride.core.domain.Violation;
import fi.hsl.parkandride.core.service.ValidationException;

@ControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value= NOT_FOUND)
    @ResponseBody
    public String notFound(HttpServletRequest req, NotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> validationException(HttpServletRequest request, ValidationException ex) {
        return handleError(request, BAD_REQUEST, ex, ex.getMessage(), ex.violations);
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> bindException(HttpServletRequest request, BindException ex) {
        List<Violation> violations = new ArrayList<>();
        for (FieldError fieldError : ex.getFieldErrors()) {
            violations.add(new Violation(fieldError.getCode(), fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return handleError(request, BAD_REQUEST, ex, "Invalid request parameters", violations);
    }

    @ExceptionHandler({ HttpRequestMethodNotSupportedException.class, HttpMediaTypeException.class })
    @ResponseBody
    public ResponseEntity<Map<String, Object>> methodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException ex) {
        return handleError(request, BAD_REQUEST, ex);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
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
        errorAttributes.put("timestamp", new Date());
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
