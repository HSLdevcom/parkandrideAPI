// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.Operator;
import fi.hsl.parkandride.core.domain.OperatorSearch;
import fi.hsl.parkandride.core.domain.SearchResults;

public interface OperatorRepository {

    long insertOperator(Operator operator);

    void updateOperator(long operatorId, Operator operator);

    Operator getOperator(long serviceId);

    SearchResults<Operator> findOperators(OperatorSearch search);

}
