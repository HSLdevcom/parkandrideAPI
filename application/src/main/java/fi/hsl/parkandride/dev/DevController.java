package fi.hsl.parkandride.dev;

import static fi.hsl.parkandride.back.ContactDao.CONTACT_ID_SEQ;
import static fi.hsl.parkandride.back.FacilityDao.FACILITY_ID_SEQ;
import static fi.hsl.parkandride.back.HubDao.HUB_ID_SEQ;
import static fi.hsl.parkandride.front.UrlSchema.DEV_CONTACTS;
import static fi.hsl.parkandride.front.UrlSchema.DEV_FACILITIES;
import static fi.hsl.parkandride.front.UrlSchema.DEV_HUBS;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysema.query.sql.RelationalPath;
import com.mysema.query.sql.postgres.PostgresQueryFactory;

import fi.hsl.parkandride.back.ContactDao;
import fi.hsl.parkandride.back.FacilityDao;
import fi.hsl.parkandride.back.HubDao;
import fi.hsl.parkandride.back.sql.*;
import fi.hsl.parkandride.core.back.ContactRepository;
import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.back.HubRepository;
import fi.hsl.parkandride.core.domain.Contact;
import fi.hsl.parkandride.core.domain.Facility;
import fi.hsl.parkandride.core.domain.Hub;
import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.service.ContactService;
import fi.hsl.parkandride.core.service.FacilityService;
import fi.hsl.parkandride.core.service.HubService;
import fi.hsl.parkandride.core.service.TransactionalWrite;

@RestController
@Profile({"dev_api"})
public class DevController {

    private static QFacility qFacility = QFacility.facility;

    private static QHub qHub = QHub.hub;

    private static QContact qContact = QContact.contact;

    @Resource PostgresQueryFactory queryFactory;

    @Resource FacilityService facilityService;

    @Resource ContactService contactService;

    @Resource FacilityRepository facilityRepository;

    @Resource HubRepository hubRepository;

    @Resource ContactRepository contactRepository;

    @Resource HubService hubService;

    @Resource JdbcTemplate jdbcTemplate;

    @RequestMapping(method = DELETE, value = DEV_FACILITIES)
    @TransactionalWrite
    public ResponseEntity<Void> deleteFacilities() {
        clear(QFacilityStatus.facilityStatus, QFacilityService.facilityService, QPort.port, QFacilityAlias.facilityAlias, QCapacity.capacity,
                QFacility.facility);
        resetSequence(FACILITY_ID_SEQ);
        return new ResponseEntity<Void>(OK);
    }

    @RequestMapping(method = DELETE, value = DEV_HUBS)
    @TransactionalWrite
    public ResponseEntity<Void> deleteHubs() {
        clear(QHubFacility.hubFacility, QHub.hub);
        resetSequence(HUB_ID_SEQ);
        return new ResponseEntity<Void>(OK);
    }

    @RequestMapping(method = DELETE, value = DEV_CONTACTS)
    @TransactionalWrite
    public ResponseEntity<Void> deleteContacts() {
        clear(QContact.contact);
        resetSequence(CONTACT_ID_SEQ);
        return new ResponseEntity<Void>(OK);
    }

    @RequestMapping(method = PUT, value = DEV_FACILITIES)
    @TransactionalWrite
    public ResponseEntity<List<Facility>> pushFacilities(@RequestBody List<Facility> facilities, User currentUser) {
        FacilityDao facilityDao = (FacilityDao) facilityRepository;
        List<Facility> results = new ArrayList<>();
        for (Facility facility : facilities) {
            if (facility.id != null) {
                facilityDao.insertFacility(facility, facility.id);
                results.add(facility);
            } else {
                results.add(facilityService.createFacility(facility, currentUser));
            }
        }
        resetSequence(FACILITY_ID_SEQ, queryFactory.from(qFacility).singleResult(qFacility.id.max()));
        return new ResponseEntity<List<Facility>>(results, OK);
    }

    @RequestMapping(method = PUT, value = DEV_HUBS)
    @TransactionalWrite
    public ResponseEntity<List<Hub>> pushHubs(@RequestBody List<Hub> hubs, User currentUser) {
        HubDao hubDao = (HubDao) hubRepository;
        List<Hub> results = new ArrayList<>();
        for (Hub hub : hubs) {
            if (hub.id != null) {
                hubDao.insertHub(hub, hub.id);
                results.add(hub);
            } else {
                results.add(hubService.createHub(hub, currentUser));
            }
        }
        resetSequence(HUB_ID_SEQ, queryFactory.from(qHub).singleResult(qHub.id.max()));
        return new ResponseEntity<List<Hub>>(results, OK);
    }

    @RequestMapping(method = PUT, value = DEV_CONTACTS)
    @TransactionalWrite
    public ResponseEntity<List<Contact>> pushContacts(@RequestBody List<Contact> contacts, User currentUser) {
        ContactDao contactDao = (ContactDao) contactRepository;
        List<Contact> results = new ArrayList<>();
        for (Contact contact : contacts) {
            if (contact.id != null) {
                contactDao.insertContact(contact, contact.id);
                results.add(contact);
            } else {
                results.add(contactService.createContact(contact, currentUser));
            }
        }
        resetSequence(CONTACT_ID_SEQ, queryFactory.from(qContact).singleResult(qContact.id.max()));
        return new ResponseEntity<List<Contact>>(results, OK);
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
