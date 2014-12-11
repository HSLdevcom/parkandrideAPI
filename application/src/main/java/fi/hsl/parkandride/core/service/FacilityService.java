package fi.hsl.parkandride.core.service;

import java.util.List;

import com.google.common.collect.Lists;

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
    public Facility createFacility(Facility facility) {
        validationService.validate(facility);
        facility.id = repository.insertFacility(facility);
        return facility;
    }

    @TransactionalWrite
    public Facility updateFacility(long facilityId, Facility facility) {
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

    @TransactionalWrite
    public void createStatuses(long facilityId, List<FacilityStatus> statuses) {
        statuses.forEach((status) -> validationService.validate(status));
        repository.insertStatuses(facilityId, statuses);
    }

    @TransactionalRead
    public List<FacilityStatus> getStatuses(long facilityId) {
        return repository.getStatuses(facilityId);
    }
}
