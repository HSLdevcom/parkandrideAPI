package fi.hsl.parkandride.core.service;

import static fi.hsl.parkandride.core.domain.Permission.HUB_CREATE;
import static fi.hsl.parkandride.core.domain.Permission.HUB_UPDATE;
import static fi.hsl.parkandride.core.service.AuthenticationService.authorize;

import fi.hsl.parkandride.core.domain.HubSearch;
import fi.hsl.parkandride.core.back.HubRepository;
import fi.hsl.parkandride.core.domain.Hub;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.User;

public class HubService {

    private final HubRepository repository;

    private final ValidationService validationService;

    public HubService(HubRepository repository, ValidationService validationService) {
        this.repository = repository;
        this.validationService = validationService;
    }

    @TransactionalWrite
    public Hub createHub(Hub hub, User currentUser) {
        authorize(currentUser, HUB_CREATE);

        validationService.validate(hub);
        return getHub(repository.insertHub(hub));
    }

    @TransactionalWrite
    public Hub updateHub(long hubId, Hub hub, User currentUser) {
        authorize(currentUser, HUB_UPDATE);

        validationService.validate(hub);
        repository.updateHub(hubId, hub);
        return getHub(hubId);
    }

    @TransactionalRead
    public Hub getHub(long hubId) {
        return repository.getHub(hubId);
    }

    @TransactionalRead
    public SearchResults<Hub> search(HubSearch search) {
        return repository.findHubs(search);
    }
}
