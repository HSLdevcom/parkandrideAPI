package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.back.ServiceRepository;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.Service;
import fi.hsl.parkandride.core.domain.ServiceSearch;

public class ServiceService {

    private final ServiceRepository repository;

    public ServiceService(ServiceRepository repository) {
        this.repository = repository;
    }

    @TransactionalRead
    public Service getService(long id) {
        return repository.getService(id);
    }

    @TransactionalRead
    public SearchResults search(ServiceSearch search) {
        return repository.findServices(search);
    }

}
