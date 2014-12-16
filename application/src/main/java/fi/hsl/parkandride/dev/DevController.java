package fi.hsl.parkandride.dev;

import static fi.hsl.parkandride.back.ContactDao.CONTACT_ID_SEQ;
import static fi.hsl.parkandride.back.FacilityDao.FACILITY_ID_SEQ;
import static fi.hsl.parkandride.back.HubDao.HUB_ID_SEQ;
import static fi.hsl.parkandride.front.UrlSchema.DEV_CONTACTS;
import static fi.hsl.parkandride.front.UrlSchema.DEV_FACILITIES;
import static fi.hsl.parkandride.front.UrlSchema.DEV_HUBS;
import static fi.hsl.parkandride.front.UrlSchema.DEV_LOGIN;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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
import fi.hsl.parkandride.core.back.UserRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.*;

@RestController
@Profile({"dev_api"})
public class DevController {

    private static QFacility qFacility = QFacility.facility;

    private static QHub qHub = QHub.hub;

    private static QContact qContact = QContact.contact;

    @Resource PostgresQueryFactory queryFactory;

    @Resource ContactService contactService;

    @Resource FacilityRepository facilityRepository;

    @Resource HubRepository hubRepository;

    @Resource ContactRepository contactRepository;

    @Resource HubService hubService;

    @Resource JdbcTemplate jdbcTemplate;

    @Resource UserService userService;

    @Resource AuthenticationService authenticationService;

    @Resource UserRepository userRepository;

    @RequestMapping(method = POST, value = DEV_LOGIN)
    public ResponseEntity<Login> login(@RequestBody NewUser newUser) {
        UserSecret userSecret;
        try {
            userSecret = userRepository.getUser(newUser.username);
            if (newUser.role != userSecret.user.role) {
                userRepository.updateUser(userSecret.user.id, newUser);
            }
            userRepository.updatePassword(userSecret.user.id, authenticationService.encryptPassword(newUser.password));
        } catch (NotFoundException e) {
            userSecret = new UserSecret();
            userSecret.user = userService.createUserNoValidate(newUser);
        }
        Login login = new Login();
        login.token = authenticationService.token(userSecret.user.id);
        login.username = userSecret.user.username;
        login.role = userSecret.user.role;
        return new ResponseEntity<>(login, OK);
    }

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
    public ResponseEntity<List<Facility>> pushFacilities(@RequestBody List<Facility> facilities) {
        FacilityDao facilityDao = (FacilityDao) facilityRepository;
        List<Facility> results = new ArrayList<>();
        for (Facility facility : facilities) {
            if (facility.id != null) {
                facilityDao.insertFacility(facility, facility.id);
            } else {
                facility.id = facilityDao.insertFacility(facility);
            }
            results.add(facility);
        }
        resetSequence(FACILITY_ID_SEQ, queryFactory.from(qFacility).singleResult(qFacility.id.max()));
        return new ResponseEntity<List<Facility>>(results, OK);
    }

    @RequestMapping(method = PUT, value = DEV_HUBS)
    @TransactionalWrite
    public ResponseEntity<List<Hub>> pushHubs(@RequestBody List<Hub> hubs) {
        HubDao hubDao = (HubDao) hubRepository;
        List<Hub> results = new ArrayList<>();
        for (Hub hub : hubs) {
            if (hub.id != null) {
                hubDao.insertHub(hub, hub.id);
            } else {
                hub.id = hubDao.insertHub(hub);
            }
            results.add(hub);
        }
        resetSequence(HUB_ID_SEQ, queryFactory.from(qHub).singleResult(qHub.id.max()));
        return new ResponseEntity<List<Hub>>(results, OK);
    }

    @RequestMapping(method = PUT, value = DEV_CONTACTS)
    @TransactionalWrite
    public ResponseEntity<List<Contact>> pushContacts(@RequestBody List<Contact> contacts) {
        ContactDao contactDao = (ContactDao) contactRepository;
        List<Contact> results = new ArrayList<>();
        for (Contact contact : contacts) {
            if (contact.id != null) {
                contactDao.insertContact(contact, contact.id);
            } else {
                contact.id = contactDao.insertContact(contact);
            }
            results.add(contact);
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
