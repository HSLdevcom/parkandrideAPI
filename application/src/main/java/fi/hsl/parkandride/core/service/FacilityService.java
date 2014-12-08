package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.domain.*;

public class FacilityService {

    private final FacilityRepository repository;

    private final ValidationService validationService;

    private final AuthService authService;

    public FacilityService(FacilityRepository repository, ValidationService validationService, AuthService authService) {
        this.repository = repository;
        this.validationService = validationService;
        this.authService = authService;
    }

    @TransactionalWrite
    public Facility createFacility(Facility facility, User currentUser) {
        authService.authorize(currentUser);
        validationService.validate(facility);
        facility.id = repository.insertFacility(facility);
        return facility;
    }

    @TransactionalWrite
    public Facility updateFacility(long facilityId, Facility facility, User currentUser) {
        authService.authorize(currentUser);
        validationService.validate(facility);
        Facility oldFacility = repository.getFacilityForUpdate(facilityId);
        repository.updateFacility(facilityId, facility, oldFacility);
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

}
