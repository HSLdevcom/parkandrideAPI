package fi.hsl.parkandride.back;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static fi.hsl.parkandride.core.domain.Sort.Dir.ASC;
import static fi.hsl.parkandride.core.domain.Sort.Dir.DESC;

import com.mysema.query.Tuple;
import com.mysema.query.dml.StoreClause;
import com.mysema.query.sql.SQLExpressions;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.sql.postgres.PostgresQuery;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.expr.ComparableExpression;
import com.mysema.query.types.expr.SimpleExpression;

import fi.hsl.parkandride.back.sql.QService;
import fi.hsl.parkandride.core.back.ServiceRepository;
import fi.hsl.parkandride.core.back.ServiceRepository;
import fi.hsl.parkandride.core.domain.Service;
import fi.hsl.parkandride.core.domain.ServiceSearch;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.Sort;
import fi.hsl.parkandride.core.domain.Violation;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.core.service.ValidationException;

public class ServiceDao implements ServiceRepository {

    private static final Sort DEFAULT_SORT = new Sort("name.fi", ASC);

    private static QService qService = QService.service;

    private static MultilingualStringMapping nameMapping = new MultilingualStringMapping(qService.nameFi, qService.nameSv, qService.nameEn);

    private static MappingProjection<Service> serviceMapping = new MappingProjection<Service>(Service.class, qService.all()) {
        @Override
        protected Service map(Tuple row) {
            Long id = row.get(qService.id);
            if (id == null) {
                return null;
            }
            Service service = new Service();
            service.id = id;
            service.name = nameMapping.map(row);
            return service;
        }
    };

    private final PostgresQueryFactory queryFactory;

    public ServiceDao(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @TransactionalRead
    public Service getService(long serviceId) {
        return queryFactory.from(qService).where(qService.id.eq(serviceId)).singleResult(serviceMapping);
    }

    @Override
    @TransactionalRead
    public SearchResults<Service> findServices(ServiceSearch search) {
        PostgresQuery qry = queryFactory.from(qService);
        qry.limit(search.limit + 1);
        qry.offset(search.offset);

        if (search.ids != null && !search.ids.isEmpty()) {
            qry.where(qService.id.in(search.ids));
        }
        if (search.name != null) {
            if (!isNullOrEmpty(search.name.fi)) {
                qry.where(qService.nameFi.startsWith(search.name.fi));
            }
            if (!isNullOrEmpty(search.name.sv)) {
                qry.where(qService.nameSv.startsWith(search.name.sv));
            }
            if (!isNullOrEmpty(search.name.en)) {
                qry.where(qService.nameEn.startsWith(search.name.en));
            }
        }

        orderBy(search.sort, qry);
        return SearchResults.of(qry.list(serviceMapping), search.limit);
    }

    private void orderBy(Sort sort, PostgresQuery qry) {
        sort = firstNonNull(sort, DEFAULT_SORT);
        ComparableExpression<String> sortField;
        switch (firstNonNull(sort.by, DEFAULT_SORT.by)) {
            case "name.fi": sortField = qService.nameFi.toUpperCase(); break;
            case "name.sv": sortField = qService.nameSv.toUpperCase(); break;
            case "name.en": sortField = qService.nameEn.toUpperCase(); break;
            default: throw invalidSortBy();
        }
        if (DESC.equals(sort.dir)) {
            qry.orderBy(sortField.desc(), qService.id.desc());
        } else {
            qry.orderBy(sortField.asc(), qService.id.asc());
        }
    }

    private ValidationException invalidSortBy() {
        return new ValidationException(new Violation("SortBy", "sort.by", "Expected one of 'name.fi', 'name.sv' or 'name.en'"));
    }

}
