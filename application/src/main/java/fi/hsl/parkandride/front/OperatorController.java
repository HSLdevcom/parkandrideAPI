// Copyright © 2015 HSL

package fi.hsl.parkandride.front;

import static fi.hsl.parkandride.front.UrlSchema.API_KEY;
import static fi.hsl.parkandride.front.UrlSchema.OPERATOR;
import static fi.hsl.parkandride.front.UrlSchema.OPERATORS;
import static fi.hsl.parkandride.front.UrlSchema.OPERATOR_ID;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.Authorization;

import fi.hsl.parkandride.core.domain.Operator;
import fi.hsl.parkandride.core.domain.OperatorSearch;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.service.OperatorService;

@RestController
@Api("operators")
public class OperatorController {

    private final Logger log = LoggerFactory.getLogger(OperatorController.class);

    @Inject
    OperatorService operatorService;

    @ApiOperation(value = "Create operator", authorizations = @Authorization(API_KEY))
    @RequestMapping(method = POST, value = OPERATORS, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Operator> createOperator(@RequestBody Operator operator,
                                                   User currentUser,
                                                   UriComponentsBuilder builder) {
        log.info("createOperator");
        Operator newOperator = operatorService.createOperator(operator, currentUser);
        log.info(format("createOperator(%s)", newOperator.id));

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path(OPERATOR).buildAndExpand(newOperator.id).toUri());
        return new ResponseEntity<>(newOperator, headers, CREATED);
    }

    @ApiOperation(value = "Update operator", authorizations = @Authorization(API_KEY))
    @RequestMapping(method = PUT, value = OPERATOR, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Operator> updateOperator(@PathVariable(OPERATOR_ID) long operatorId,
                                                   @RequestBody Operator operator,
                                                   User currentUser) {
        log.info(format("updateOperator(%s)", operatorId));
        Operator response = operatorService.updateOperator(operatorId, operator, currentUser);
        return new ResponseEntity<>(response, OK);
    }

    @ApiOperation(value = "Get operator details")
    @RequestMapping(method = GET, value = OPERATOR, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Operator> getOperator(@PathVariable(OPERATOR_ID) long operatorId) {
        Operator operator = operatorService.getOperator(operatorId);
        return new ResponseEntity<>(operator, OK);
    }

    @ApiOperation(value = "Find operators")
    @RequestMapping(method = GET, value = OPERATORS, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchResults<Operator>> findOperators(OperatorSearch search) {
        SearchResults<Operator> results = operatorService.search(search);
        return new ResponseEntity<>(results, OK);
    }

}
