package fi.hsl.parkandride.dev;

import static fi.hsl.parkandride.inbound.UrlSchema.TEST_FACILITIES;
import static fi.hsl.parkandride.inbound.UrlSchema.TEST_HUBS;
import static fi.hsl.parkandride.outbound.FacilityDao.FACILITY_ID_SEQ;
import static fi.hsl.parkandride.outbound.HubDao.HUB_ID_SEQ;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mysema.query.sql.RelationalPath;
import com.mysema.query.sql.postgres.PostgresQueryFactory;

import fi.hsl.parkandride.core.domain.Facility;
import fi.hsl.parkandride.core.domain.Hub;
import fi.hsl.parkandride.core.outbound.FacilityRepository;
import fi.hsl.parkandride.core.outbound.HubRepository;
import fi.hsl.parkandride.core.service.FacilityService;
import fi.hsl.parkandride.core.service.HubService;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.outbound.FacilityDao;
import fi.hsl.parkandride.outbound.HubDao;
import fi.hsl.parkandride.outbound.sql.QCapacity;
import fi.hsl.parkandride.outbound.sql.QFacility;
import fi.hsl.parkandride.outbound.sql.QFacilityAlias;
import fi.hsl.parkandride.outbound.sql.QHub;
import fi.hsl.parkandride.outbound.sql.QHubFacility;

@Controller
@Profile("e2e")
public class TestController {

    private static QFacility qFacility = QFacility.facility;

    private static QHub qHub = QHub.hub;

    @Resource PostgresQueryFactory queryFactory;

    @Resource FacilityService facilityService;

    @Resource FacilityRepository facilityRepository;

    @Resource HubRepository hubRepository;

    @Resource HubService hubService;

    @Resource JdbcTemplate jdbcTemplate;

    @RequestMapping(method = DELETE, value = TEST_FACILITIES)
    @TransactionalWrite
    public ResponseEntity<Void> deleteFacilities() {
        clear(QFacilityAlias.facilityAlias, QCapacity.capacity, QFacility.facility);
        resetSequence(FACILITY_ID_SEQ);
        return new ResponseEntity<Void>(OK);
    }

    @RequestMapping(method = DELETE, value = TEST_HUBS)
    @TransactionalWrite
    public ResponseEntity<Void> deleteHubs() {
        clear(QHubFacility.hubFacility, QHub.hub);
        resetSequence(HUB_ID_SEQ);
        return new ResponseEntity<Void>(OK);
    }

    @RequestMapping(method = PUT, value = TEST_FACILITIES)
    @TransactionalWrite
    public ResponseEntity<List<Facility>> pushFacilities(@RequestBody List<Facility> facilities) {
        FacilityDao facilityDao = (FacilityDao) facilityRepository;
        List<Facility> results = new ArrayList<>();
        for (Facility facility : facilities) {
            if (facility.id != null) {
                facilityDao.insertFacility(facility, facility.id);
                results.add(facility);
            } else {
                results.add(facilityService.createFacility(facility));
            }
        }
        resetSequence(FACILITY_ID_SEQ, queryFactory.from(qFacility).singleResult(qFacility.id.max()));
        return new ResponseEntity<List<Facility>>(results, OK);
    }

    @RequestMapping(method = PUT, value = TEST_HUBS)
    @TransactionalWrite
    public ResponseEntity<List<Hub>> pushHubs(@RequestBody List<Hub> hubs) {
        HubDao hubDao = (HubDao) hubRepository;
        List<Hub> results = new ArrayList<>();
        for (Hub hub : hubs) {
            if (hub.id != null) {
                hubDao.insertHub(hub, hub.id);
                results.add(hub);
            } else {
                results.add(hubService.createHub(hub));
            }
        }
        resetSequence(HUB_ID_SEQ, queryFactory.from(qHub).singleResult(qHub.id.max()));
        return new ResponseEntity<List<Hub>>(results, OK);
    }

    private void clear(RelationalPath... tables) {
        for (RelationalPath table : tables) {
            queryFactory.delete(table).execute();
        }
    }

    private void resetSequence(String sequence) {
        resetSequence(sequence, 0l);
    }
    private void resetSequence(String sequence, Long currentMax) {
        if (currentMax == null) {
            currentMax = 0l;
        }
        jdbcTemplate.execute(format("drop sequence %s", sequence));
        jdbcTemplate.execute(format("create sequence %s increment by 1 start with %s", sequence, currentMax+1));
    }

}
