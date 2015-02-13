package fi.hsl.parkandride.front;

import static fi.hsl.parkandride.front.UrlSchema.*;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Arrays;
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

    @RequestMapping(method = GET, value = FACILITY_STATUSES)
    public ResponseEntity<List<FacilityStatus>> facilityStatuses() {
        return new ResponseEntity<>(asList(FacilityStatus.values()), OK);
    }

    @RequestMapping(method = GET, value = PRICING_METHODS)
    public ResponseEntity<List<PricingMethod>> pricingMethods() {
        return new ResponseEntity<>(asList(PricingMethod.values()), OK);
    }

    @RequestMapping(method = GET, value = ROLES, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Role>> roles() {
        return new ResponseEntity<>(asList(Role.values()), OK);
    }
}
