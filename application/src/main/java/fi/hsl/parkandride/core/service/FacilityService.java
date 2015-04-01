// Copyright © 2015 HSL

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.back.ContactRepository;
import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.*;

import java.util.*;

import static fi.hsl.parkandride.core.domain.Permission.*;
import static fi.hsl.parkandride.core.service.AuthenticationService.authorize;

public class FacilityService {

    private final FacilityRepository repository;
    private final UtilizationRepository utilizationRepository;
    private final ContactRepository contactRepository;
    private final ValidationService validationService;
    private final PredictionService predictionService;

    public FacilityService(FacilityRepository repository, UtilizationRepository utilizationRepository, ContactRepository contactRepository, ValidationService validationService, PredictionService predictionService) {
        this.repository = repository;
        this.utilizationRepository = utilizationRepository;
        this.contactRepository = contactRepository;
        this.validationService = validationService;
        this.predictionService = predictionService;
    }

    @TransactionalWrite
    public Facility createFacility(Facility facility, User currentUser) {
        authorize(currentUser, facility, FACILITY_CREATE);
        validate(facility);

        return getFacility(repository.insertFacility(facility));
    }

    @TransactionalWrite
    public Facility updateFacility(long facilityId, Facility facility, User currentUser) {
        // User has update right to the input data...
        authorize(currentUser, facility, FACILITY_UPDATE);
        // ...and to the facility being updated
        Facility oldFacility = repository.getFacilityForUpdate(facilityId);
        authorize(currentUser, oldFacility, FACILITY_UPDATE);

        validate(facility);

        repository.updateFacility(facilityId, facility, oldFacility);
        return getFacility(facilityId);
    }

    private void validate(Facility facility) {
        Collection<Violation> violations = new ArrayList<>();
        validationService.validate(facility, violations);
        CapacityPricingValidator.validateAndNormalize(facility, violations);
        validateContact(facility.operatorId, facility.contacts.emergency, "emergency", violations);
        validateContact(facility.operatorId, facility.contacts.operator, "operator", violations);
        validateContact(facility.operatorId, facility.contacts.service, "service", violations);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }
    }

    private void validateContact(Long facilityOperatorId, Long contactId, String contactType, Collection<Violation> violations) {
        if (contactId != null) {
            Contact contact = contactRepository.getContact(contactId);
            if (contact == null) {
                violations.add(new Violation("NotFound", "contacts." + contactType, "contact not found"));
            } else if (contact.operatorId != null && !contact.operatorId.equals(facilityOperatorId)) {
                violations.add(new Violation("OperatorMismatch", "contacts." + contactType, "operator should match facility operator"));
            }
        }
    }

    @TransactionalRead
    public Facility getFacility(long id) {
        return repository.getFacility(id);
    }

    @TransactionalRead
    public SearchResults<FacilityInfo> search(PageableFacilitySearch search) {
        return repository.findFacilities(search);
    }

    @TransactionalRead
    public FacilitySummary summarize(FacilitySearch search) {
        return repository.summarizeFacilities(search);
    }

    @TransactionalWrite
    public void registerUtilization(long facilityId, List<Utilization> utilization, User currentUser) {
        authorize(currentUser, repository.getFacilityInfo(facilityId), FACILITY_UTILIZATION_UPDATE);

        initUtilizationDefaults(facilityId, utilization);
        utilization.forEach(u -> validateUtilization(u, facilityId));
        predictionService.registerUtilizations(utilization);
    }

    private static void initUtilizationDefaults(long facilityId, List<Utilization> utilization) {
        utilization.stream()
                .filter(u -> u.facilityId == null)
                .forEach(u -> u.facilityId = facilityId);
    }

    private void validateUtilization(Utilization u, long expectedFacilityId) {
        List<Violation> violations = new ArrayList<>();
        validationService.validate(u, violations);
        if (!Objects.equals(u.facilityId, expectedFacilityId)) {
            violations.add(new Violation("NotEqual", "facilityId", "Expected to be " + expectedFacilityId + " but was " + u.facilityId));
        }
        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }
    }

    @TransactionalRead
    public Set<Utilization> findLatestUtilization(long facilityId) {
        return utilizationRepository.findLatestUtilization(facilityId);
    }
}
