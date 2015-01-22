package fi.hsl.parkandride.front;

import static fi.hsl.parkandride.front.UrlSchema.CAPACITY_TYPES;
import static fi.hsl.parkandride.front.UrlSchema.DAY_TYPES;
import static fi.hsl.parkandride.front.UrlSchema.PAYMENT_METHODS;
import static fi.hsl.parkandride.front.UrlSchema.SERVICES;
import static fi.hsl.parkandride.front.UrlSchema.USAGES;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fi.hsl.parkandride.core.domain.*;

@Controller
public class SchemaController {

    @RequestMapping(method = GET, value = CAPACITY_TYPES)
    public ResponseEntity<List<CapacityType>> capacityTypes() {
        return new ResponseEntity<>(asList(CapacityType.values()), OK);
    }

    @RequestMapping(method = GET, value = USAGES)
    public ResponseEntity<List<Usage>> usages() {
        return new ResponseEntity<>(asList(Usage.values()), OK);
    }

    @RequestMapping(method = GET, value = DAY_TYPES)
    public ResponseEntity<List<DayType>> dayTypes() {
        return new ResponseEntity<>(asList(DayType.values()), OK);
    }

    @RequestMapping(method = GET, value = SERVICES)
    public ResponseEntity<List<Service>> services() {
        return new ResponseEntity<>(asList(Service.values()), OK);
    }

    @RequestMapping(method = GET, value = PAYMENT_METHODS)
    public ResponseEntity<List<PaymentMethod>> paymentMethods() {
        return new ResponseEntity<>(asList(PaymentMethod.values()), OK);
    }

}
