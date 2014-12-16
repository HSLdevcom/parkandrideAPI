package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.back.OperatorRepository;
import fi.hsl.parkandride.core.domain.Operator;
import fi.hsl.parkandride.core.domain.OperatorSearch;
import fi.hsl.parkandride.core.domain.SearchResults;

public class OperatorService {

    private final OperatorRepository repository;

    public OperatorService(OperatorRepository repository) {
        this.repository = repository;
    }

    @TransactionalRead
    public Operator getOperator(long id) {
        return repository.getOperator(id);
    }

    @TransactionalRead
    public SearchResults search(OperatorSearch search) {
        return repository.findOperators(search);
    }

}
