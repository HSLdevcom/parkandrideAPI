package fi.hsl.parkandride.front;

import static fi.hsl.parkandride.front.UrlSchema.OPERATOR;
import static fi.hsl.parkandride.front.UrlSchema.OPERATORS;
import static fi.hsl.parkandride.front.UrlSchema.OPERATOR_ID;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.hsl.parkandride.core.domain.Operator;
import fi.hsl.parkandride.core.domain.OperatorSearch;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.service.OperatorService;

@RestController
public class OperatorController {

    @Inject
    OperatorService operatorService;

    @RequestMapping(method = GET, value = OPERATOR, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Operator> getOperator(@PathVariable(OPERATOR_ID) long operatorId) {
        Operator operator = operatorService.getOperator(operatorId);
        return new ResponseEntity<>(operator, OK);
    }

    @RequestMapping(method = GET, value = OPERATORS, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchResults<Operator>> findOperators(OperatorSearch search) {
        SearchResults<Operator> results = operatorService.search(search);
        return new ResponseEntity<>(results, OK);
    }

}
