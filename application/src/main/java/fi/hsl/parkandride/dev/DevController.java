// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.dev;

import static fi.hsl.parkandride.front.UrlSchema.*;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.hsl.parkandride.FeatureProfile;
import fi.hsl.parkandride.back.ContactDao;
import fi.hsl.parkandride.back.FacilityDao;
import fi.hsl.parkandride.back.HubDao;
import fi.hsl.parkandride.back.OperatorDao;
import fi.hsl.parkandride.core.back.ContactRepository;
import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.back.HubRepository;
import fi.hsl.parkandride.core.back.OperatorRepository;
import fi.hsl.parkandride.core.back.UserRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.AuthenticationService;
import fi.hsl.parkandride.core.service.ContactService;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.core.service.UserService;

@RestController
@Profile({ FeatureProfile.DEV_API})
public class DevController {

    @Resource ContactService contactService;

    @Resource FacilityRepository facilityRepository;

    @Resource HubRepository hubRepository;

    @Resource ContactRepository contactRepository;

    @Resource OperatorRepository operatorRepository;

    @Resource DevHelper devHelper;

    @Resource UserService userService;

    @Resource AuthenticationService authenticationService;

    @Resource UserRepository userRepository;

    @RequestMapping(method = POST, value = DEV_LOGIN)
    public ResponseEntity<Login> login(@RequestBody NewUser newUser) {
        User user = devHelper.createOrUpdateUser(newUser);
        Login login = devHelper.login(user.username);
        return new ResponseEntity<>(login, OK);
    }

    @RequestMapping(method = DELETE, value = DEV_FACILITIES)
    @TransactionalWrite
    public ResponseEntity<Void> deleteFacilities() {
        devHelper.deleteFacilities();
        return new ResponseEntity<>(OK);
    }

    @RequestMapping(method = DELETE, value = DEV_HUBS)
    @TransactionalWrite
    public ResponseEntity<Void> deleteHubs() {
        devHelper.deleteHubs();
        return new ResponseEntity<>(OK);
    }

    @RequestMapping(method = DELETE, value = DEV_CONTACTS)
    @TransactionalWrite
    public ResponseEntity<Void> deleteContacts() {
        devHelper.deleteContacts();
        return new ResponseEntity<>(OK);
    }

    @RequestMapping(method = DELETE, value = DEV_OPERATORS)
    @TransactionalWrite
    public ResponseEntity<Void> deleteOperators() {
        devHelper.deleteOperators();
        return new ResponseEntity<>(OK);
    }

    @RequestMapping(method = DELETE, value = DEV_USERS)
    @TransactionalWrite
    public ResponseEntity<Void> deleteUsers() {
        devHelper.deleteUsers();
        return new ResponseEntity<>(OK);
    }

    @RequestMapping(method = PUT, value = DEV_USERS)
    @TransactionalWrite
    public ResponseEntity<List<User>> pushUsers(@RequestBody List<NewUser> newUsers) {
        List<User> users = new ArrayList<>(newUsers.size());
        for (NewUser newUser : newUsers) {
            users.add(devHelper.createOrUpdateUser(newUser));
        }
        return new ResponseEntity<>(users, OK);
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
        devHelper.resetFacilitySequence();
        return new ResponseEntity<>(results, OK);
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
        devHelper.resetHubSequence();
        return new ResponseEntity<>(results, OK);
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
        devHelper.resetContactSequence();
        return new ResponseEntity<>(results, OK);
    }

    @RequestMapping(method = PUT, value = DEV_OPERATORS)
    @TransactionalWrite
    public ResponseEntity<List<Operator>> pushOperators(@RequestBody List<Operator> operators) {
        OperatorDao operatorDao = (OperatorDao) operatorRepository;
        List<Operator> results = new ArrayList<>();
        for (Operator operator : operators) {
            if (operator.id != null) {
                operatorDao.insertOperator(operator, operator.id);
            } else {
                operator.id = operatorDao.insertOperator(operator);
            }
            results.add(operator);
        }
        devHelper.resetOperatorSequence();
        return new ResponseEntity<>(results, OK);
    }
}
