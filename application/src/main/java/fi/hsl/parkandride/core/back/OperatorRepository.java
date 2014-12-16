package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.Operator;
import fi.hsl.parkandride.core.domain.OperatorSearch;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.service.TransactionalWrite;

public interface OperatorRepository {

    long insertOperator(Operator operator);

    void updateOperator(long operatorId, Operator operator);

    Operator getOperator(long serviceId);

    SearchResults<Operator> findOperators(OperatorSearch search);

}
