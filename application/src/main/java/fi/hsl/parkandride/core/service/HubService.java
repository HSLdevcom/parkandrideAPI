package fi.hsl.parkandride.core.service;

import static fi.hsl.parkandride.core.domain.Role.ADMIN;

import fi.hsl.parkandride.core.back.HubRepository;
import fi.hsl.parkandride.core.domain.Hub;
import fi.hsl.parkandride.core.domain.Role;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.SpatialSearch;
import fi.hsl.parkandride.core.domain.User;

public class HubService {

    private final HubRepository repository;

    private final ValidationService validationService;

    private final AuthService authService;

    public HubService(HubRepository repository, ValidationService validationService, AuthService authService) {
        this.repository = repository;
        this.validationService = validationService;
        this.authService = authService;
    }

    @TransactionalWrite
    public Hub createHub(Hub hub, User currentUser) {
        authService.authorize(currentUser, ADMIN);
        validationService.validate(hub);
        hub.id = repository.insertHub(hub);
        return hub;
    }

    @TransactionalWrite
    public Hub updateHub(long hubId, Hub hub, User currentUser) {
        authService.authorize(currentUser, ADMIN);
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
