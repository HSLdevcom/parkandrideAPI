package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.domain.Hub;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.SpatialSearch;
import fi.hsl.parkandride.core.outbound.HubRepository;

public class HubService {

    private final HubRepository repository;

    public HubService(HubRepository repository) {
        this.repository = repository;
    }

    @TransactionalWrite
    public Hub createHub(Hub hub) {
        hub.id = repository.insertHub(hub);
        return hub;
    }

    @TransactionalWrite
    public Hub updateHub(long hubId, Hub hub) {
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
