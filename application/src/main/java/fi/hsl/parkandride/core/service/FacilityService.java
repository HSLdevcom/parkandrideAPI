package fi.hsl.parkandride.core.service;

import java.util.List;

import fi.hsl.parkandride.core.domain.Facility;
import fi.hsl.parkandride.core.domain.FacilitySearch;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.outbound.FacilityRepository;

public class FacilityService {

    private final FacilityRepository repository;

    public FacilityService(FacilityRepository repository) {
        this.repository = repository;
    }

    @TransactionalWrite
    public Facility createFacility(Facility facility) {
        facility.id = repository.insertFacility(facility);
        return facility;
    }

    @TransactionalWrite
    public Facility updateFacility(long facilityId, Facility facility) {
        // XXX: oldFacility = getFacilityForUpdate
        repository.updateFacility(facilityId, facility);
        return facility;
    }

    @TransactionalRead
    public Facility getFacility(long id) {
        return repository.getFacility(id);
    }

    @TransactionalRead
    public SearchResults search(FacilitySearch search) {
        return repository.findFacilities(search);
    }

}
