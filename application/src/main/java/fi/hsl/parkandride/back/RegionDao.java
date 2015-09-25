// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import com.mysema.query.Tuple;
import com.mysema.query.sql.postgres.PostgresQuery;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.expr.ComparableExpression;
import fi.hsl.parkandride.back.sql.QRegion;
import fi.hsl.parkandride.core.domain.Region;
import fi.hsl.parkandride.core.service.TransactionalRead;
import java.util.Collection;

public class RegionDao implements RegionRepository {

    private static final QRegion qRegion = QRegion.region;

    private static final MultilingualStringMapping nameMapping = new MultilingualStringMapping(qRegion.nameFi, qRegion.nameSv, qRegion.nameEn);

    private static final MappingProjection<Region> regionMapping = new MappingProjection<Region>(Region.class, qRegion.all()) {

        @Override
        protected Region map(Tuple row) {
            Region region = new Region();
            region.id = row.get(qRegion.id);
            region.area = row.get(qRegion.area);
            region.name = nameMapping.map(row);
            return region;
        }
    };


    private final PostgresQueryFactory queryFactory;

    public RegionDao(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @TransactionalRead
    public Collection<Region> getRegions() {
        PostgresQuery qry = queryFactory.from(qRegion);

        ComparableExpression<String> sortField = qRegion.nameFi.lower();
        qry.orderBy(sortField.asc());

        return qry.list(regionMapping);
    }
}
