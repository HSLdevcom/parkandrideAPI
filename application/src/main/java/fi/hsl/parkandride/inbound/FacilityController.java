package fi.hsl.parkandride.inbound;

import static fi.hsl.parkandride.inbound.Paths.API;
import static fi.hsl.parkandride.inbound.Paths.CAPACITY_TYPES;
import static fi.hsl.parkandride.inbound.Paths.FACILITIES;
import static fi.hsl.parkandride.inbound.Paths.FACILITY;
import static fi.hsl.parkandride.inbound.Paths.FACILITY_ID;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;

import javax.inject.Inject;

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

    @RequestMapping(method = POST, value = FACILITIES)
    public ResponseEntity<Facility> createFacility(@RequestBody Facility facility, UriComponentsBuilder builder) {
        Facility newFacility = facilityService.createFacility(facility);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path(FACILITY).buildAndExpand(newFacility.id).toUri());
        return new ResponseEntity<>(newFacility, headers, CREATED);
    }

    @RequestMapping(method = GET, value = FACILITY)
    public ResponseEntity<Facility> getFacility(@PathVariable(FACILITY_ID) long facilityId) {
        Facility facility = facilityService.getFacility(facilityId);
        return new ResponseEntity<>(facility, OK);
    }

    @RequestMapping(method = PUT, value = FACILITY)
    public ResponseEntity<Facility> updateFacility(@PathVariable(FACILITY_ID) long facilityId,
                                                   @RequestBody Facility facility) {
        Facility response = facilityService.updateFacility(facilityId, facility);
        return new ResponseEntity<>(facility, OK);
    }

    @RequestMapping(method = GET, value = FACILITIES)
    public ResponseEntity<SearchResults<Facility>> findFacilities(PageableSpatialSearchDto search) {
        SearchResults<Facility> results = facilityService.search(search.toSpatialSearch());
        return new ResponseEntity<>(results, OK);
    }

    // TODO: REMOVE - this method is only for demo/testing in the beginning of the project
    @RequestMapping(method = POST, value = API + "/generate-test-facilities")
    public ResponseEntity<SearchResults<Facility>> generateTestFacilities() {
        SearchResults<Facility> results = facilityService.generateTestData();
        return new ResponseEntity<>(results, OK);
    }

    @RequestMapping(method = GET, value = CAPACITY_TYPES)
    public ResponseEntity<List<CapacityType>> capacityTypes() {
        List<CapacityType> types = asList(CapacityType.values());
        return new ResponseEntity<>(types, OK);
    }

}
