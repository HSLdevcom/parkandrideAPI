package fi.hsl.parkandride.inbound;

import static fi.hsl.parkandride.inbound.FeatureResults.TO_FEATURE;
import static fi.hsl.parkandride.inbound.UrlSchema.GEOJSON;
import static fi.hsl.parkandride.inbound.UrlSchema.HUB;
import static fi.hsl.parkandride.inbound.UrlSchema.HUBS;
import static fi.hsl.parkandride.inbound.UrlSchema.HUB_ID;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import javax.inject.Inject;

import org.geolatte.common.Feature;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import fi.hsl.parkandride.core.domain.Hub;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.service.HubService;

@Controller
public class HubController {

    @Inject
    HubService hubService;

    @RequestMapping(method = POST, value = HUBS, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Hub> createHub(@RequestBody Hub hub, UriComponentsBuilder builder) {
        Hub newHub = hubService.createHub(hub);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path(HUB).buildAndExpand(newHub.id).toUri());
        return new ResponseEntity<>(newHub, headers, CREATED);
    }

    @RequestMapping(method = GET, value = HUB, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Hub> getHub(@PathVariable(HUB_ID) long hubId) {
        Hub hub = hubService.getHub(hubId);
        return new ResponseEntity<>(hub, OK);
    }

    @RequestMapping(method = GET, value = HUB, produces = GEOJSON)
    public ResponseEntity<Feature> getHubAsFeature(@PathVariable(HUB_ID) long hubId) {
        Hub hub = hubService.getHub(hubId);
        return new ResponseEntity<Feature>(TO_FEATURE.apply(hub), OK);
    }

    @RequestMapping(method = PUT, value = HUB, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Hub> updateHub(@PathVariable(HUB_ID) long hubId,
                                         @RequestBody Hub hub) {
        Hub response = hubService.updateHub(hubId, hub);
        return new ResponseEntity<>(hub, OK);
    }

    @RequestMapping(method = GET, value = HUBS, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchResults<Hub>> findHubs(SpatialSearchDto search) {
        SearchResults<Hub> results = hubService.search(search.toSpatialSearch());
        return new ResponseEntity<>(results, OK);
    }

    @RequestMapping(method = GET, value = HUBS, produces = GEOJSON)
    public ResponseEntity<FeatureResults> findHubsAsFeatureCollection(SpatialSearchDto search) {
        SearchResults<Hub> results = hubService.search(search.toSpatialSearch());
        return new ResponseEntity<>(FeatureResults.of(results), OK);
    }

}
