package fi.hsl.parkandride.inbound;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import fi.hsl.parkandride.core.domain.NotFoundException;
import fi.hsl.parkandride.core.service.ValidationException;

@ControllerAdvice
public class ExceptionHandlers {

    @Inject
    BasicErrorController errorController;

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value= NOT_FOUND)
    @ResponseBody
    public String notFound(HttpServletRequest req, NotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> validationException(HttpServletRequest request, ValidationException ex) {
        request.setAttribute("javax.servlet.error.status_code", BAD_REQUEST.value());
        ResponseEntity<Map<String, Object>> responseEntity = errorController.error(request);
        responseEntity.getBody().put("violations", ex.violations);
        return responseEntity;
    }

}
