package fi.hsl.parkandride.core.service;

import static fi.hsl.parkandride.core.domain.Permission.FACILITY_CREATE;
import static fi.hsl.parkandride.core.domain.Permission.FACILITY_STATUS_UPDATE;
import static fi.hsl.parkandride.core.domain.Permission.FACILITY_UPDATE;
import static fi.hsl.parkandride.core.domain.Role.ADMIN;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR_API;
import static fi.hsl.parkandride.core.service.AuthenticationService.authorize;

import java.util.List;
import java.util.Objects;

import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.domain.*;

public class FacilityService {

    private final FacilityRepository repository;

    private final ValidationService validationService;

    public FacilityService(FacilityRepository repository, ValidationService validationService) {
        this.repository = repository;
        this.validationService = validationService;
    }

    @TransactionalWrite
    public Facility createFacility(Facility facility, User currentUser) {
        authorize(currentUser, facility, FACILITY_CREATE);

        validationService.validate(facility);
        facility.id = repository.insertFacility(facility);
        return facility;
    }

    @TransactionalWrite
    public Facility updateFacility(long facilityId, Facility facility, User currentUser) {
        authorize(currentUser, facility, FACILITY_UPDATE);

        validationService.validate(facility);
        Facility oldFacility = repository.getFacilityForUpdate(facilityId);
        repository.updateFacility(facilityId, facility, oldFacility);
        facility.id = facilityId;
        return facility;
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
