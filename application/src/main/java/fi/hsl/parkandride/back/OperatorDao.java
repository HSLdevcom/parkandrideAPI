package fi.hsl.parkandride.back;

import static com.google.common.base.MoreObjects.firstNonNull;
import static fi.hsl.parkandride.core.domain.Sort.Dir.ASC;
import static fi.hsl.parkandride.core.domain.Sort.Dir.DESC;

import com.mysema.query.Tuple;
import com.mysema.query.sql.SQLExpressions;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.postgres.PostgresQuery;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.expr.ComparableExpression;
import com.mysema.query.types.expr.SimpleExpression;

import fi.hsl.parkandride.back.sql.QOperator;
import fi.hsl.parkandride.core.back.OperatorRepository;
import fi.hsl.parkandride.core.domain.Operator;
import fi.hsl.parkandride.core.domain.OperatorSearch;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.Sort;
import fi.hsl.parkandride.core.domain.Violation;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.core.service.ValidationException;

public class OperatorDao implements OperatorRepository {

    public static final String OPERATOR_ID_SEQ = "user_id_seq";

    private static final SimpleExpression<Long> nextOperatorId = SQLExpressions.nextval(OPERATOR_ID_SEQ);

    private static final Sort DEFAULT_SORT = new Sort("name.fi", ASC);

    private static QOperator qOperator = QOperator.operator;

    private static MultilingualStringMapping nameMapping = new MultilingualStringMapping(qOperator.nameFi, qOperator.nameSv, qOperator.nameEn);

    private static MappingProjection<Operator> operatorMapping = new MappingProjection<Operator>(Operator.class, qOperator.all()) {
        @Override
        protected Operator map(Tuple row) {
            Long id = row.get(qOperator.id);
            if (id == null) {
                return null;
            }
            Operator operator = new Operator();
            operator.id = id;
            operator.name = nameMapping.map(row);
            return operator;
        }
    };

    private final PostgresQueryFactory queryFactory;

    public OperatorDao(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @TransactionalWrite
    @Override
    public long insertOperator(Operator operator) {
        return insertOperator(operator, queryFactory.query().singleResult(nextOperatorId));
    }

    @TransactionalWrite
    public long insertOperator(Operator operator, long id) {
        SQLInsertClause insert = queryFactory.insert(qOperator);
        nameMapping.populate(operator.name, insert);
        insert.execute();
        return id;
    }

    @Override
    @TransactionalRead
    public Operator getOperator(long operatorId) {
        return queryFactory.from(qOperator).where(qOperator.id.eq(operatorId)).singleResult(operatorMapping);
    }

    @Override
    @TransactionalRead
    public SearchResults<Operator> findOperators(OperatorSearch search) {
        PostgresQuery qry = queryFactory.from(qOperator);
        qry.limit(search.limit + 1);
        qry.offset(search.offset);
        orderBy(search.sort, qry);
        return SearchResults.of(qry.list(operatorMapping), search.limit);
    }

    private void orderBy(Sort sort, PostgresQuery qry) {
        sort = firstNonNull(sort, DEFAULT_SORT);
        ComparableExpression<String> sortField;
        switch (firstNonNull(sort.by, DEFAULT_SORT.by)) {
            case "name.fi": sortField = qOperator.nameFi.toUpperCase(); break;
            case "name.sv": sortField = qOperator.nameSv.toUpperCase(); break;
            case "name.en": sortField = qOperator.nameEn.toUpperCase(); break;
            default: throw invalidSortBy();
        }
        if (DESC.equals(sort.dir)) {
            qry.orderBy(sortField.desc(), qOperator.id.desc());
        } else {
            qry.orderBy(sortField.asc(), qOperator.id.asc());
        }
    }

    private ValidationException invalidSortBy() {
        return new ValidationException(new Violation("SortBy", "sort.by", "Expected one of 'name.fi', 'name.sv' or 'name.en'"));
    }

}
