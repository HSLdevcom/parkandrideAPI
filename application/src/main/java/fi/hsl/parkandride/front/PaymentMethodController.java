package fi.hsl.parkandride.front;

import static fi.hsl.parkandride.front.UrlSchema.PAYMENT_METHODS;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;

import fi.hsl.parkandride.core.domain.PaymentMethod;
import fi.hsl.parkandride.core.service.PaymentMethodService;

@RestController
@Api("payment-methods")
public class PaymentMethodController {

    @Inject
    private PaymentMethodService service;

    @RequestMapping(method = GET, value = PAYMENT_METHODS, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Results<PaymentMethod>> findAll() {
        return new ResponseEntity<>(Results.of(service.findAll()), OK);
    }

}
