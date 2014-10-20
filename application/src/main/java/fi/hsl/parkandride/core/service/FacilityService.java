package fi.hsl.parkandride.core.service;

import java.util.ArrayList;
import java.util.List;

import org.geolatte.geom.Polygon;
import org.geolatte.geom.codec.Wkt;

import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.outbound.FacilityRepository;

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

}
