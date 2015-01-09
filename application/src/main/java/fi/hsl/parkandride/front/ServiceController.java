package fi.hsl.parkandride.front;

import static fi.hsl.parkandride.front.UrlSchema.SERVICE;
import static fi.hsl.parkandride.front.UrlSchema.SERVICES;
import static fi.hsl.parkandride.front.UrlSchema.SERVICE_ID;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;

import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.Service;
import fi.hsl.parkandride.core.domain.ServiceSearch;
import fi.hsl.parkandride.core.service.ServiceService;

@RestController
@Api("services")
public class ServiceController {

    @Inject
    ServiceService serviceService;

    @RequestMapping(method = GET, value = SERVICE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Service> getService(@PathVariable(SERVICE_ID) long serviceId) {
        Service service = serviceService.getService(serviceId);
        return new ResponseEntity<>(service, OK);
    }

    @RequestMapping(method = GET, value = SERVICES, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchResults<Service>> findServices(ServiceSearch search) {
        SearchResults<Service> results = serviceService.search(search);
        return new ResponseEntity<>(results, OK);
    }

}
