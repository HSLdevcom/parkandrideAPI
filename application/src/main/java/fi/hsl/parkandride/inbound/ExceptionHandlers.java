package fi.hsl.parkandride.inbound;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
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
    @ResponseStatus(value = BAD_REQUEST)
    @ResponseBody
    public List<Violation> validationException(ValidationException ex) {
        return ex.violations;
    }

}
