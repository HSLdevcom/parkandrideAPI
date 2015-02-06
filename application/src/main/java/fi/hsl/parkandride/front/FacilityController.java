package fi.hsl.parkandride.front;

import static fi.hsl.parkandride.front.UrlSchema.*;
import static fi.hsl.parkandride.front.geojson.FeatureCollection.FACILITY_TO_FEATURE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;

import javax.inject.Inject;

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

import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.FacilityService;
import fi.hsl.parkandride.front.geojson.Feature;
import fi.hsl.parkandride.front.geojson.FeatureCollection;

@RestController
@Api("facilities")
public class FacilityController {

    @Inject
    FacilityService facilityService;

    @ApiOperation(value = "Create facility", authorizations = @Authorization(API_KEY))
    @RequestMapping(method = POST, value = FACILITIES, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Facility> createFacility(@RequestBody Facility facility,
                                                   User currentUser,
                                                   UriComponentsBuilder builder) {
        Facility newFacility = facilityService.createFacility(facility, currentUser);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path(FACILITY).buildAndExpand(newFacility.id).toUri());
        return new ResponseEntity<>(newFacility, headers, CREATED);
    }

    @RequestMapping(method = GET, value = FACILITY, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Facility> getFacility(@PathVariable(FACILITY_ID) long facilityId) {
        Facility facility = facilityService.getFacility(facilityId);
        return new ResponseEntity<>(facility, OK);
    }

    @RequestMapping(method = GET, value = FACILITY, produces = GEOJSON)
    public ResponseEntity<Feature> getFacilityAsFeature(@PathVariable(FACILITY_ID) long facilityId) {
        Facility facility = facilityService.getFacility(facilityId);
        return new ResponseEntity<>(FACILITY_TO_FEATURE.apply(facility), OK);
    }

    @ApiOperation(value = "Update facility", authorizations = @Authorization(API_KEY))
    @RequestMapping(method = PUT, value = FACILITY, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Facility> updateFacility(@PathVariable(FACILITY_ID) long facilityId,
                                                   @RequestBody Facility facility,
                                                   User currentUser) {
        Facility response = facilityService.updateFacility(facilityId, facility, currentUser);
        return new ResponseEntity<>(response, OK);
    }

    @RequestMapping(method = GET, value = FACILITIES, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchResults<FacilityInfo>> findFacilities(PageableFacilitySearch search) {
        SearchResults<FacilityInfo> results = facilityService.search(search);
        return new ResponseEntity<>(results, OK);
    }

    @RequestMapping(method = GET, value = FACILITIES, params = "summary", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<FacilitySummary> summarizeFacilities(FacilitySearch search) {
        FacilitySummary summary = facilityService.summarize(search);
        return new ResponseEntity<>(summary, OK);
    }

    @RequestMapping(method = GET, value = FACILITIES, produces = GEOJSON)
    public ResponseEntity<FeatureCollection> findFacilitiesAsFeatureCollection(PageableFacilitySearch search) {
        SearchResults<FacilityInfo> results = facilityService.search(search);
        return new ResponseEntity<>(FeatureCollection.ofFacilities(results), OK);
    }

    @ApiOperation(value = "Update facility status", authorizations = @Authorization(API_KEY))
    @RequestMapping(method = PUT, value = FACILITY_UTILIZATION, produces = APPLICATION_JSON_VALUE)
    public void registerUtilization(@PathVariable(FACILITY_ID) long facilityId,
                                    @RequestBody List<Utilization> statuses,
                                    User currentUser) {
        facilityService.registerUtilization(facilityId, statuses, currentUser);
    }

    // FIXME: Only latest utilization...
    @RequestMapping(method = GET, value = FACILITY_UTILIZATION, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Results<Utilization>> getUtilization(@PathVariable(FACILITY_ID) long facilityId) {
        List<Utilization> statuses = facilityService.getStatuses(facilityId);
        return new ResponseEntity<>(Results.of(statuses), OK);
    }
}
