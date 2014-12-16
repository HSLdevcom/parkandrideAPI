package fi.hsl.parkandride.front;

import static fi.hsl.parkandride.front.FeatureCollection.FACILITY_TO_FEATURE;
import static fi.hsl.parkandride.front.UrlSchema.*;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.Instant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.collect.Lists;

import fi.hsl.parkandride.core.domain.CapacityType;
import fi.hsl.parkandride.core.domain.Facility;
import fi.hsl.parkandride.core.domain.FacilityStatus;
import fi.hsl.parkandride.core.domain.FacilitySummary;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.service.AuthService;
import fi.hsl.parkandride.core.service.FacilityService;

@RestController
public class FacilityController {

    @Inject
    FacilityService facilityService;

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
        return new ResponseEntity<Feature>(FACILITY_TO_FEATURE.apply(facility), OK);
    }

    @RequestMapping(method = PUT, value = FACILITY, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Facility> updateFacility(@PathVariable(FACILITY_ID) long facilityId,
                                                   @RequestBody Facility facility,
                                                   User currentUser) {
        Facility response = facilityService.updateFacility(facilityId, facility, currentUser);
        return new ResponseEntity<>(facility, OK);
    }

    @RequestMapping(method = GET, value = FACILITIES, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchResults<Facility>> findFacilities(PageableSpatialSearchDto search) {
        SearchResults<Facility> results = facilityService.search(search.toSpatialSearch());
        return new ResponseEntity<>(results, OK);
    }

    @RequestMapping(method = GET, value = FACILITIES, params = "summary", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<FacilitySummary> summarizeFacilities(SpatialSearchDto search) {
        FacilitySummary summary = facilityService.summarize(search.toSpatialSearch());
        return new ResponseEntity<>(summary, OK);
    }

    @RequestMapping(method = GET, value = FACILITIES, produces = GEOJSON)
    public ResponseEntity<FeatureCollection> findFacilitiesAsFeatureCollection(PageableSpatialSearchDto search) {
        SearchResults<Facility> results = facilityService.search(search.toSpatialSearch());
        return new ResponseEntity<>(FeatureCollection.ofFacilities(results), OK);
    }

    @RequestMapping(method = GET, value = CAPACITY_TYPES)
    public ResponseEntity<SearchResults<CapacityType>> capacityTypes() {
        List<CapacityType> types = asList(CapacityType.values());
        return new ResponseEntity<>(SearchResults.of(types), OK);
    }

    @RequestMapping(method = PUT, value = FACILITY_STATUS, produces = APPLICATION_JSON_VALUE)
    public void createStatuses(@PathVariable(FACILITY_ID) long facilityId, @RequestBody List<FacilityStatus> statuses) {
        facilityService.createStatuses(facilityId, statuses);
    }

    @RequestMapping(method = GET, value = FACILITY_STATUS, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Results<FacilityStatus>> getStatuses(@PathVariable(FACILITY_ID) long facilityId) {
        List<FacilityStatus> statuses = facilityService.getStatuses(facilityId);
        return new ResponseEntity<>(Results.of(statuses), OK);
    }
}
