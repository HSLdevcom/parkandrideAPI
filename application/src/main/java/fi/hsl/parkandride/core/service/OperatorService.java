package fi.hsl.parkandride.core.service;

import static fi.hsl.parkandride.core.domain.Role.ADMIN;

import fi.hsl.parkandride.core.back.OperatorRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.domain.Operator;

public class OperatorService {

    private final OperatorRepository repository;

    private final ValidationService validationService;

    private final AuthService authService;

    public OperatorService(OperatorRepository repository, ValidationService validationService, AuthService authService) {
        this.repository = repository;
        this.validationService = validationService;
        this.authService = authService;
    }

    @TransactionalWrite
    public Operator createOperator(Operator operator, User currentUser) {
        authService.authorize(currentUser, ADMIN);
        validationService.validate(operator);
        operator.id = repository.insertOperator(operator);
        return operator;
    }

    @TransactionalWrite
    public Operator updateOperator(long operatorId, Operator operator, User currentUser) {
        authService.authorize(currentUser, ADMIN);
        validationService.validate(operator);
        repository.updateOperator(operatorId, operator);
        return operator;
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
