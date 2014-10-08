package fi.hsl.parkandride.inbound;

import static fi.hsl.parkandride.inbound.FeatureResults.TO_FEATURE;
import static fi.hsl.parkandride.inbound.UrlSchema.*;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;

import javax.inject.Inject;

import org.geolatte.common.Feature;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import fi.hsl.parkandride.core.domain.CapacityType;
import fi.hsl.parkandride.core.domain.Facility;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.service.FacilityService;

@Controller
public class FacilityController {

    @Inject
    FacilityService facilityService;

    @RequestMapping(method = POST, value = FACILITIES, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Facility> createFacility(@RequestBody Facility facility, UriComponentsBuilder builder) {
        Facility newFacility = facilityService.createFacility(facility);

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
        return new ResponseEntity<Feature>(TO_FEATURE.apply(facility), OK);
    }

    @RequestMapping(method = PUT, value = FACILITY, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Facility> updateFacility(@PathVariable(FACILITY_ID) long facilityId,
                                                   @RequestBody Facility facility) {
        Facility response = facilityService.updateFacility(facilityId, facility);
        return new ResponseEntity<>(facility, OK);
    }

    @RequestMapping(method = GET, value = FACILITIES, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchResults<Facility>> findFacilities(PageableSpatialSearchDto search) {
        SearchResults<Facility> results = facilityService.search(search.toSpatialSearch());
        return new ResponseEntity<>(results, OK);
    }

    @RequestMapping(method = GET, value = FACILITIES, produces = GEOJSON)
    public ResponseEntity<FeatureResults> findFacilitiesAsFeatureCollection(PageableSpatialSearchDto search) {
        SearchResults<Facility> results = facilityService.search(search.toSpatialSearch());
        return new ResponseEntity<>(FeatureResults.of(results), OK);
    }

    @RequestMapping(method = GET, value = CAPACITY_TYPES)
    public ResponseEntity<SearchResults<CapacityType>> capacityTypes() {
        List<CapacityType> types = asList(CapacityType.values());
        return new ResponseEntity<>(SearchResults.of(types), OK);
    }


    // TODO: REMOVE - this method is only for demo/testing in the beginning of the project
    @RequestMapping(method = POST, value = API + "/generate-test-facilities")
    public ResponseEntity<SearchResults<Facility>> generateTestFacilities() {
        SearchResults<Facility> results = facilityService.generateTestData();
        return new ResponseEntity<>(results, OK);
    }

}
