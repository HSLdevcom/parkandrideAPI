package fi.hsl.parkandride.core.service;

import static fi.hsl.parkandride.core.domain.Permission.FACILITY_CREATE;
import static fi.hsl.parkandride.core.domain.Permission.FACILITY_UPDATE;
import static fi.hsl.parkandride.core.service.AuthenticationService.authorize;
import static java.util.Collections.sort;

import java.util.*;

import fi.hsl.parkandride.core.back.ContactRepository;
import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.domain.*;

public class FacilityService {

    private final FacilityRepository repository;

    private final ValidationService validationService;

    private final ContactRepository contactRepository;

    public FacilityService(FacilityRepository repository, ContactRepository contactRepository, ValidationService validationService) {
        this.repository = repository;
        this.contactRepository = contactRepository;
        this.validationService = validationService;
    }

    @TransactionalWrite
    public Facility createFacility(Facility facility, User currentUser) {
        authorize(currentUser, facility, FACILITY_CREATE);
        validate(facility);

        facility.id = repository.insertFacility(facility);
        facility.initialize();
        return facility;
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
        facility.id = facilityId;
        facility.initialize();
        return facility;
    }

    private void validate(Facility facility) {
        Collection<Violation> violations = new ArrayList<>();
        validationService.validate(facility, violations);
        CapacityPricingValidator.validate(facility.builtCapacity, facility.pricing, facility.unavailableCapacities, violations);
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
            if (contact.operatorId != null && !contact.operatorId.equals(facilityOperatorId)) {
                violations.add(new Violation("OperatorMismatch", "contacts." + contactType, "operator should match facility operator"));
            }
        }
    }

    @TransactionalRead
    public Facility getFacility(long id) {
        return repository.getFacility(id);
    }

    @TransactionalRead
    public SearchResults search(PageableSpatialSearch search) {
        return repository.findFacilities(search);
    }

    @TransactionalRead
    public FacilitySummary summarize(SpatialSearch search) {
        return repository.summarizeFacilities(search);
    }

    @TransactionalWrite
    public void createStatuses(long facilityId, List<FacilityStatus> statuses, User currentUser) {
        // TODO: authorize(currentUser, facility, FACILITY_STATUS_UPDATE);

        statuses.forEach((status) -> validationService.validate(status));
        repository.insertStatuses(facilityId, statuses);
    }

    @TransactionalRead
    public List<FacilityStatus> getStatuses(long facilityId) {
        return repository.getStatuses(facilityId);
    }
}
