// Copyright © 2015 HSL

package fi.hsl.parkandride.front;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.Authorization;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.FacilityService;
import fi.hsl.parkandride.front.geojson.Feature;
import fi.hsl.parkandride.front.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

import static fi.hsl.parkandride.front.UrlSchema.*;
import static fi.hsl.parkandride.front.geojson.FeatureCollection.FACILITY_TO_FEATURE;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@Api("facilities")
public class FacilityController {

    private final Logger log = LoggerFactory.getLogger(FacilityController.class);

    @Inject
    FacilityService facilityService;

    @ApiOperation(value = "Create facility", authorizations = @Authorization(API_KEY))
    @RequestMapping(method = POST, value = FACILITIES, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Facility> createFacility(@RequestBody Facility facility,
                                                   User currentUser,
                                                   UriComponentsBuilder builder) {
        log.info("createFacility");
        Facility newFacility = facilityService.createFacility(facility, currentUser);
        log.info("createFacility(%s)", newFacility.id);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path(FACILITY).buildAndExpand(newFacility.id).toUri());
        return new ResponseEntity<>(newFacility, headers, CREATED);
    }

    @ApiOperation(value = "Get facility details")
    @RequestMapping(method = GET, value = FACILITY, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Facility> getFacility(@PathVariable(FACILITY_ID) long facilityId) {
        log.info(format("getFacility(%s)", facilityId));
        Facility facility = facilityService.getFacility(facilityId);
        return new ResponseEntity<>(facility, OK);
    }

    @ApiOperation(value = "Get facility info as GeoJSON Feature")
    @RequestMapping(method = GET, value = FACILITY, produces = GEOJSON)
    public ResponseEntity<Feature> getFacilityAsFeature(@PathVariable(FACILITY_ID) long facilityId) {
        log.info(format("getFacilityAsFeature(%s)", facilityId));
        Facility facility = facilityService.getFacility(facilityId);
        return new ResponseEntity<>(FACILITY_TO_FEATURE.apply(facility), OK);
    }

    @ApiOperation(value = "Update facility", authorizations = @Authorization(API_KEY))
    @RequestMapping(method = PUT, value = FACILITY, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Facility> updateFacility(@PathVariable(FACILITY_ID) long facilityId,
                                                   @RequestBody Facility facility,
                                                   User currentUser) {
        log.info(format("updateFacility(%s)", facilityId));
        Facility response = facilityService.updateFacility(facilityId, facility, currentUser);
        return new ResponseEntity<>(response, OK);
    }

    @ApiOperation(value = "Find facilities")
    @RequestMapping(method = GET, value = FACILITIES, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchResults<FacilityInfo>> findFacilities(PageableFacilitySearch search) {
        log.info("findFacilities");
        SearchResults<FacilityInfo> results = facilityService.search(search);
        return new ResponseEntity<>(results, OK);
    }

    @ApiOperation(value = "Summarize built capacity of matching facilities")
    @RequestMapping(method = GET, value = FACILITIES, params = "summary", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<FacilitySummary> summarizeFacilities(FacilitySearch search) {
        log.info("summarizeFacilities");
        FacilitySummary summary = facilityService.summarize(search);
        return new ResponseEntity<>(summary, OK);
    }

    @ApiOperation(value = "Find facilities as GeoJSON FeatureCollection")
    @RequestMapping(method = GET, value = FACILITIES, produces = GEOJSON)
    public ResponseEntity<FeatureCollection> findFacilitiesAsFeatureCollection(PageableFacilitySearch search) {
        log.info("findFacilitiesAsFeatureCollection");
        SearchResults<FacilityInfo> results = facilityService.search(search);
        return new ResponseEntity<>(FeatureCollection.ofFacilities(results), OK);
    }

    @ApiOperation(value = "Update facility utilization status", authorizations = @Authorization(API_KEY))
    @RequestMapping(method = PUT, value = FACILITY_UTILIZATION, produces = APPLICATION_JSON_VALUE)
    public void registerUtilization(@PathVariable(FACILITY_ID) long facilityId,
                                    @RequestBody List<Utilization> statuses,
                                    User currentUser) {
        log.info(format("registerUtilization(%s)", facilityId));
        facilityService.registerUtilization(facilityId, statuses, currentUser);
    }

    @RequestMapping(method = GET, value = FACILITY_UTILIZATION, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Results<Utilization>> getUtilization(@PathVariable(FACILITY_ID) long facilityId) {
        log.info(format("getUtilization(%s)", facilityId));
        Set<Utilization> utilizations = facilityService.findLatestUtilization(facilityId);
        return new ResponseEntity<>(Results.of(utilizations), OK);
    }
}
