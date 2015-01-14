package fi.hsl.parkandride.core.service;

import static fi.hsl.parkandride.core.domain.Permission.OPERATOR_CREATE;
import static fi.hsl.parkandride.core.domain.Permission.OPERATOR_UPDATE;
import static fi.hsl.parkandride.core.domain.Role.ADMIN;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR;
import static fi.hsl.parkandride.core.service.AuthenticationService.authorize;

import fi.hsl.parkandride.core.back.OperatorRepository;
import fi.hsl.parkandride.core.domain.Operator;
import fi.hsl.parkandride.core.domain.OperatorSearch;
import fi.hsl.parkandride.core.domain.Permission;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.User;

public class OperatorService {

    private final OperatorRepository repository;

    private final ValidationService validationService;

    public OperatorService(OperatorRepository repository, ValidationService validationService) {
        this.repository = repository;
        this.validationService = validationService;
    }

    @TransactionalWrite
    public Operator createOperator(Operator operator, User currentUser) {
        authorize(currentUser, OPERATOR_CREATE);

        validationService.validate(operator);
        operator.id = repository.insertOperator(operator);
        return operator;
    }

    @TransactionalWrite
    public Operator updateOperator(long operatorId, Operator operator, User currentUser) {
        operator.id = operatorId;
        authorize(currentUser, operator, OPERATOR_UPDATE);

        validationService.validate(operator);
        repository.updateOperator(operatorId, operator);
        operator.id = operatorId;
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
