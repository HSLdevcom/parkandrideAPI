// Copyright © 2015 HSL

package fi.hsl.parkandride.front;

import static fi.hsl.parkandride.front.UrlSchema.API_KEY;
import static fi.hsl.parkandride.front.UrlSchema.GEOJSON;
import static fi.hsl.parkandride.front.UrlSchema.HUB;
import static fi.hsl.parkandride.front.UrlSchema.HUBS;
import static fi.hsl.parkandride.front.UrlSchema.HUB_ID;
import static fi.hsl.parkandride.front.geojson.FeatureCollection.HUB_TO_FEATURE;
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

import fi.hsl.parkandride.core.domain.Hub;
import fi.hsl.parkandride.core.domain.HubSearch;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.service.HubService;
import fi.hsl.parkandride.front.geojson.Feature;
import fi.hsl.parkandride.front.geojson.FeatureCollection;

@RestController
@Api("hubs")
public class HubController {

    private final Logger log = LoggerFactory.getLogger(HubController.class);

    @Inject
    HubService hubService;

    @ApiOperation(value = "Create hub", authorizations = @Authorization(API_KEY))
    @RequestMapping(method = POST, value = HUBS, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Hub> createHub(@RequestBody Hub hub, User currentUser, UriComponentsBuilder builder) {
        log.info("createHub");
        Hub newHub = hubService.createHub(hub, currentUser);
        log.info(format("createHub(%s)", newHub.id));

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path(HUB).buildAndExpand(newHub.id).toUri());
        return new ResponseEntity<>(newHub, headers, CREATED);
    }

    @ApiOperation(value = "Get hub details")
    @RequestMapping(method = GET, value = HUB, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Hub> getHub(@PathVariable(HUB_ID) long hubId) {
        log.info(format("getHub(%s)", hubId));
        Hub hub = hubService.getHub(hubId);
        return new ResponseEntity<>(hub, OK);
    }

    @ApiOperation(value = "Get hub details as GeoJSON Feature")
    @RequestMapping(method = GET, value = HUB, produces = GEOJSON)
    public ResponseEntity<Feature> getHubAsFeature(@PathVariable(HUB_ID) long hubId) {
        log.info(format("getHubAsFeature(%s)", hubId));
        Hub hub = hubService.getHub(hubId);
        return new ResponseEntity<Feature>(HUB_TO_FEATURE.apply(hub), OK);
    }

    @ApiOperation(value = "Update hub", authorizations = @Authorization(API_KEY))
    @RequestMapping(method = PUT, value = HUB, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Hub> updateHub(@PathVariable(HUB_ID) long hubId,
                                         @RequestBody Hub hub,
                                         User currentUser) {
        log.info(format("updateHub(%s)", hubId));
        Hub response = hubService.updateHub(hubId, hub, currentUser);
        return new ResponseEntity<>(hub, OK);
    }

    @ApiOperation(value = "Find hubs")
    @RequestMapping(method = GET, value = HUBS, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchResults<Hub>> findHubs(HubSearch search) {
        log.info("findHubs");
        SearchResults<Hub> results = hubService.search(search);
        return new ResponseEntity<>(results, OK);
    }

    @ApiOperation(value = "Find hubs as GeoJSON FeatureCollection")
    @RequestMapping(method = GET, value = HUBS, produces = GEOJSON)
    public ResponseEntity<FeatureCollection> findHubsAsFeatureCollection(HubSearch search) {
        log.info("findHubsAsFeatureCollection");
        SearchResults<Hub> results = hubService.search(search);
        return new ResponseEntity<>(FeatureCollection.ofHubs(results), OK);
    }

}
