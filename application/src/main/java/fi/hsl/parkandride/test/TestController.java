package fi.hsl.parkandride.test;

import static fi.hsl.parkandride.inbound.UrlSchema.TEST_FACILITIES;
import static fi.hsl.parkandride.inbound.UrlSchema.TEST_HUBS;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mysema.query.sql.RelationalPath;
import com.mysema.query.sql.postgres.PostgresQueryFactory;

import fi.hsl.parkandride.core.domain.Facility;
import fi.hsl.parkandride.core.domain.Hub;
import fi.hsl.parkandride.core.service.FacilityService;
import fi.hsl.parkandride.core.service.HubService;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.outbound.sql.QCapacity;
import fi.hsl.parkandride.outbound.sql.QFacility;
import fi.hsl.parkandride.outbound.sql.QFacilityAlias;
import fi.hsl.parkandride.outbound.sql.QHub;
import fi.hsl.parkandride.outbound.sql.QHubFacility;

@Controller
@Profile("e2e")
public class TestController {

    @Resource PostgresQueryFactory queryFactory;

    @Resource FacilityService facilityService;

    @Resource HubService hubService;

    @RequestMapping(method = DELETE, value = TEST_FACILITIES)
    @TransactionalWrite
    public ResponseEntity<Void> deleteFacilities() {
        clear(QFacilityAlias.facilityAlias, QCapacity.capacity, QFacility.facility);
        return new ResponseEntity<Void>(OK);
    }

    @RequestMapping(method = DELETE, value = TEST_HUBS)
    @TransactionalWrite
    public ResponseEntity<Void> deleteHubs() {
        clear(QHubFacility.hubFacility, QHub.hub);
        return new ResponseEntity<Void>(OK);
    }

    @RequestMapping(method = PUT, value = TEST_FACILITIES)
    @TransactionalWrite
    public ResponseEntity<List<Facility>> pushFacilities(@RequestBody List<Facility> facilities) {
        List<Facility> results = new ArrayList<>();
        for (Facility facility : facilities) {
            results.add(facilityService.createFacility(facility));
        }
        return new ResponseEntity<List<Facility>>(results, OK);
    }

    @RequestMapping(method = PUT, value = TEST_HUBS)
    @TransactionalWrite
    public ResponseEntity<List<Hub>> pushHubs(@RequestBody List<Hub> hubs) {
        List<Hub> results = new ArrayList<>();
        for (Hub hub : hubs) {
            results.add(hubService.createHub(hub));
        }
        return new ResponseEntity<List<Hub>>(results, OK);
    }

    private void clear(RelationalPath... tables) {
        for (RelationalPath table : tables) {
            queryFactory.delete(table).execute();
        }
    }

}
