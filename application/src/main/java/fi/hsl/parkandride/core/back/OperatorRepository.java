package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.Operator;
import fi.hsl.parkandride.core.domain.OperatorSearch;
import fi.hsl.parkandride.core.domain.SearchResults;

public interface OperatorRepository {

    Operator getOperator(long serviceId);

    SearchResults<Operator> findOperators(OperatorSearch search);

}
