// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.dev;

import com.google.common.collect.Lists;
import fi.hsl.parkandride.FeatureProfile;
import fi.hsl.parkandride.back.ContactDao;
import fi.hsl.parkandride.back.FacilityDao;
import fi.hsl.parkandride.back.HubDao;
import fi.hsl.parkandride.back.OperatorDao;
import fi.hsl.parkandride.core.back.*;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.*;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.ReadablePeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static fi.hsl.parkandride.core.domain.CapacityType.*;
import static fi.hsl.parkandride.front.UrlSchema.*;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

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

    @Resource PredictionService predictionService;

    @Resource FacilityService facilityService;

    @Resource UtilizationRepository utilizationRepository;

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

    @RequestMapping(method = PUT, value = DEV_UTILIZATION)
    @TransactionalWrite
    public ResponseEntity<Void> generateUtilizationData(@NotNull @PathVariable(FACILITY_ID) Long facilityId) {
        final Facility facility = facilityRepository.getFacility(facilityId);

        // Generate dummy usage for the last month
        final Random random = new Random();
        final List<Utilization> utilizations = StreamSupport.stream(
                spliteratorUnknownSize(new DateTimeIterator(DateTime.now().minusMonths(1), DateTime.now(), Minutes.minutes(5)), Spliterator.ORDERED), false)
                .flatMap(ts -> Stream.of(CAR, DISABLED, ELECTRIC_CAR)
                        .filter(facility.builtCapacity::containsKey)
                        .flatMap(capacityType -> {
                            final Stream.Builder<UtilizationKey> builder = Stream.builder();
                            builder.add(new UtilizationKey(facilityId, capacityType, Usage.PARK_AND_RIDE));
                            if (capacityType == CAR) {
                                builder.add(new UtilizationKey(facilityId, capacityType, Usage.HSL_TRAVEL_CARD));
                            }
                            return builder.build();
                        })
                        .map(utilizationKey -> newUtilization(
                                utilizationKey,
                                facility.builtCapacity.get(utilizationKey.capacityType),
                                ts.minusSeconds(random.nextInt(180)) // Randomness to prevent timestamps for different capacity types being equal
                        )))
                        .collect(toList());
        utilizationRepository.insertUtilizations(utilizations);
        predictionService.signalUpdateNeeded(utilizations);
        return new ResponseEntity<>(CREATED);
    }

    @RequestMapping(method = PUT, value = DEV_PREDICTION_HISTORY)
    @TransactionalRead // each call to predictionService.updatePredictionsHistoryForFacility creates a separate write transaction to avoid too long transactions
    public ResponseEntity<Void> generatePredictionHistory(@NotNull @PathVariable(FACILITY_ID) Long facilityId) {
        facilityRepository.getFacility(facilityId); // ensure facility exists
        final UtilizationSearch utilizationSearch = new UtilizationSearch();
        utilizationSearch.start = DateTime.now().minusWeeks(5);
        utilizationSearch.end = DateTime.now();
        utilizationSearch.facilityIds = Collections.singleton(facilityId);
        final List<Utilization> utilizations = Lists.newArrayList(utilizationRepository.findUtilizations(utilizationSearch));
        DateTime lastTimestamp = utilizations.stream().map(u -> u.timestamp).max(DateTime::compareTo).orElse(utilizationSearch.end);
        StreamSupport.stream(
                spliteratorUnknownSize(new DateTimeIterator(utilizationSearch.start.plusWeeks(4),
                        lastTimestamp.minus(PredictionRepository.PREDICTION_RESOLUTION), // avoid collision with scheduled predictions
                        PredictionRepository.PREDICTION_RESOLUTION), Spliterator.ORDERED), false)
                .map(endTime -> utilizations.stream().filter(utilization -> utilization.timestamp.isBefore(endTime)).collect(toList()))
                .forEach(utilizationList -> predictionService.updatePredictionsHistoryForFacility(utilizationList));
        return new ResponseEntity<>(CREATED);
    }

    @RequestMapping(method = PUT, value = DEV_PREDICTION)
    public ResponseEntity<Void> triggerPrediction() {
        predictionService.updatePredictions();
        return new ResponseEntity<>(NO_CONTENT);
    }

    private static Utilization newUtilization(UtilizationKey utilizationKey, Integer maxCapacity, DateTime time) {
        final Utilization u = new Utilization();
        u.capacityType = utilizationKey.capacityType;
        u.facilityId = utilizationKey.facilityId;
        u.timestamp = time;
        u.usage = utilizationKey.usage;
        u.spacesAvailable = sineWaveUtilization(time, maxCapacity);
        return u;
    }

    private static Integer sineWaveUtilization(DateTime d, Integer maxCapacity) {
        // Peaks at 16, so we subtract 16 hours.
        final double x = (d.minusHours(16).getMinuteOfDay() * 2.0 * Math.PI) / (24.0 * 60.0);
        // 1 + cos(x) is in range 0..2 so we have to divide the max capacity by 2
        final double usedSpaces = (maxCapacity / 2.0) * (1 + Math.cos(x));
        return (int)(maxCapacity - usedSpaces);
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

    private static class DateTimeIterator implements Iterator<DateTime> {
        private DateTime current;
        private final DateTime end;
        private final ReadablePeriod interval;

        public DateTimeIterator(DateTime start, DateTime end, ReadablePeriod interval) {
            Assert.state(start.isBefore(end), "Start date must be before end date");
            this.current = start;
            this.end = end;
            this.interval = interval;
        }

        @Override
        public boolean hasNext() {
            return current.isBefore(end);
        }

        @Override
        public DateTime next() {
            final DateTime returnable = this.current;
            this.current = this.current.plus(interval);
            return returnable;
        }
    }

}
