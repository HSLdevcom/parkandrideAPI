// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.front;

import fi.hsl.parkandride.core.domain.Hub;
import fi.hsl.parkandride.core.domain.HubSearch;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.domain.prediction.HubPredictionResult;
import fi.hsl.parkandride.core.domain.prediction.PredictionRequest;
import fi.hsl.parkandride.core.domain.prediction.PredictionResult;
import fi.hsl.parkandride.core.service.HubService;
import fi.hsl.parkandride.core.service.PredictionService;
import fi.hsl.parkandride.front.geojson.Feature;
import fi.hsl.parkandride.front.geojson.FeatureCollection;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

import static fi.hsl.parkandride.front.UrlSchema.*;
import static fi.hsl.parkandride.front.geojson.FeatureCollection.HUB_TO_FEATURE;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class HubController {

    private final Logger log = LoggerFactory.getLogger(HubController.class);

    @Inject
    HubService hubService;

    @Inject
    PredictionService predictionService;

    @RequestMapping(method = POST, value = HUBS, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Hub> createHub(@RequestBody Hub hub, User currentUser, UriComponentsBuilder builder) {
        log.info("createHub");
        Hub newHub = hubService.createHub(hub, currentUser);
        log.info("createHub({})", newHub.id);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path(HUB).buildAndExpand(newHub.id).toUri());
        return new ResponseEntity<>(newHub, headers, CREATED);
    }

    @RequestMapping(method = GET, value = HUB, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Hub> getHub(@PathVariable(HUB_ID) long hubId) {
        log.info("getHub({})", hubId);
        Hub hub = hubService.getHub(hubId);
        return new ResponseEntity<>(hub, OK);
    }

    @RequestMapping(method = GET, value = HUB, produces = GEOJSON)
    public ResponseEntity<Feature> getHubAsFeature(@PathVariable(HUB_ID) long hubId) {
        log.info("getHubAsFeature({})", hubId);
        Hub hub = hubService.getHub(hubId);
        return new ResponseEntity<>(HUB_TO_FEATURE.apply(hub), OK);
    }

    @RequestMapping(method = PUT, value = HUB, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Hub> updateHub(@PathVariable(HUB_ID) long hubId,
                                         @RequestBody Hub hub,
                                         User currentUser) {
        log.info("updateHub({})", hubId);
        Hub updated = hubService.updateHub(hubId, hub, currentUser);
        return new ResponseEntity<>(updated, OK);
    }

    @RequestMapping(method = GET, value = HUBS, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchResults<Hub>> findHubs(HubSearch search) {
        log.info("findHubs");
        SearchResults<Hub> results = hubService.search(search);
        return new ResponseEntity<>(results, OK);
    }

    @RequestMapping(method = GET, value = HUBS, produces = GEOJSON)
    public ResponseEntity<FeatureCollection> findHubsAsFeatureCollection(HubSearch search) {
        log.info("findHubsAsFeatureCollection");
        SearchResults<Hub> results = hubService.search(search);
        return new ResponseEntity<>(FeatureCollection.ofHubs(results), OK);
    }

    @RequestMapping(method = GET, value = HUB_PREDICTION, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HubPredictionResult>> getPrediction(@PathVariable(HUB_ID) long hubId,
                                                                   @ModelAttribute @Valid PredictionRequest request) {
        DateTime time = request.requestedTime();
        log.info("getPrediction({}, {})", hubId, time);
        final List<HubPredictionResult> results = hubService.getHub(hubId).facilityIds
                .stream()
                .flatMap(facilityId -> predictionService.getPredictionsByFacility(facilityId, time).stream())
                .flatMap(pb -> PredictionResult.from(pb).stream())
                .collect(groupingBy(result -> result.capacityType.name() + result.usage.name()))
                .values().stream()
                .map(list -> HubPredictionResult.sumFrom(hubId, list))
                .collect(toList());
        return new ResponseEntity<>(results, OK);
    }
}
