// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.front;

import static fi.hsl.parkandride.front.UrlSchema.*;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wordnik.swagger.annotations.ApiOperation;

import fi.hsl.parkandride.core.domain.*;

@Controller
public class SchemaController {

    @ApiOperation(value = "List CapacityType enum values")
    @RequestMapping(method = GET, value = CAPACITY_TYPES)
    public ResponseEntity<List<CapacityType>> capacityTypes() {
        return new ResponseEntity<>(asList(CapacityType.values()), OK);
    }

    @ApiOperation(value = "List Usage enum values")
    @RequestMapping(method = GET, value = USAGES)
    public ResponseEntity<List<Usage>> usages() {
        return new ResponseEntity<>(asList(Usage.values()), OK);
    }

    @ApiOperation(value = "List DayType enum values")
    @RequestMapping(method = GET, value = DAY_TYPES)
    public ResponseEntity<List<DayType>> dayTypes() {
        return new ResponseEntity<>(asList(DayType.values()), OK);
    }

    @ApiOperation(value = "List Service enum values")
    @RequestMapping(method = GET, value = SERVICES)
    public ResponseEntity<List<Service>> services() {
        return new ResponseEntity<>(asList(Service.values()), OK);
    }

    @ApiOperation(value = "List PaymentMethod enum values")
    @RequestMapping(method = GET, value = PAYMENT_METHODS)
    public ResponseEntity<List<PaymentMethod>> paymentMethods() {
        return new ResponseEntity<>(asList(PaymentMethod.values()), OK);
    }

    @ApiOperation(value = "List FacilityStatus enum values")
    @RequestMapping(method = GET, value = FACILITY_STATUSES)
    public ResponseEntity<List<FacilityStatus>> facilityStatuses() {
        return new ResponseEntity<>(asList(FacilityStatus.values()), OK);
    }

    @ApiOperation(value = "List PricingMethod enum values")
    @RequestMapping(method = GET, value = PRICING_METHODS)
    public ResponseEntity<List<PricingMethod>> pricingMethods() {
        return new ResponseEntity<>(asList(PricingMethod.values()), OK);
    }

    @ApiOperation(value = "List Role enum values, used in authorization")
    @RequestMapping(method = GET, value = ROLES, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Role>> roles() {
        return new ResponseEntity<>(asList(Role.values()), OK);
    }

}
