package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.back.HubRepository;
import fi.hsl.parkandride.core.domain.Hub;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.SpatialSearch;

public class HubService {

    private final HubRepository repository;

    private final ValidationService validationService;

    public HubService(HubRepository repository, ValidationService validationService) {
        this.repository = repository;
        this.validationService = validationService;
    }

    @TransactionalWrite
    public Hub createHub(Hub hub) {
        validationService.validate(hub);
        hub.id = repository.insertHub(hub);
        return hub;
    }

    @TransactionalWrite
    public Hub updateHub(long hubId, Hub hub) {
        validationService.validate(hub);
        repository.updateHub(hubId, hub);
        return hub;
    }

    @TransactionalRead
    public Hub getHub(long hubId) {
        return repository.getHub(hubId);
    }

    @TransactionalRead
    public SearchResults<Hub> search(SpatialSearch search) {
        return repository.findHubs(search);
    }
}
