package fi.hsl.parkandride.front;

import static fi.hsl.parkandride.front.UrlSchema.CAPACITY_TYPES;
import static fi.hsl.parkandride.front.UrlSchema.DAY_TYPES;
import static fi.hsl.parkandride.front.UrlSchema.USAGES;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fi.hsl.parkandride.core.domain.CapacityType;
import fi.hsl.parkandride.core.domain.DayType;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.Usage;

@Controller
public class SchemaController {

    @RequestMapping(method = GET, value = CAPACITY_TYPES)
    public ResponseEntity<SearchResults<CapacityType>> capacityTypes() {
        List<CapacityType> types = asList(CapacityType.values());
        return new ResponseEntity<>(SearchResults.of(types), OK);
    }

    @RequestMapping(method = GET, value = USAGES)
    public ResponseEntity<SearchResults<Usage>> usages() {
        List<Usage> types = asList(Usage.values());
        return new ResponseEntity<>(SearchResults.of(types), OK);
    }

    @RequestMapping(method = GET, value = DAY_TYPES)
    public ResponseEntity<SearchResults<DayType>> dayTypes() {
        List<DayType> types = asList(DayType.values());
        return new ResponseEntity<>(SearchResults.of(types), OK);
    }


}
