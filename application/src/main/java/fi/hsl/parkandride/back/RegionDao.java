// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import com.querydsl.core.Tuple;
import com.querydsl.sql.postgresql.PostgreSQLQuery;
import com.querydsl.sql.postgresql.PostgreSQLQueryFactory;
import com.querydsl.core.types.MappingProjection;
import com.querydsl.core.types.dsl.ComparableExpression;
import fi.hsl.parkandride.back.sql.QHub;
import fi.hsl.parkandride.back.sql.QRegion;
import fi.hsl.parkandride.core.domain.Region;
import fi.hsl.parkandride.core.domain.RegionWithHubs;
import fi.hsl.parkandride.core.service.TransactionalRead;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.mysema.query.group.GroupBy.groupBy;
import static com.mysema.query.group.GroupBy.set;
import static com.querydsl.spatial.GeometryExpressions.dwithin;
import static java.util.stream.Collectors.toList;

public class RegionDao implements RegionRepository {

    private static final QRegion qRegion = QRegion.region;
    private static final QHub qHub = QHub.hub;

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


    private final PostgreSQLQueryFactory queryFactory;

    public RegionDao(PostgreSQLQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @TransactionalRead
    public Collection<Region> getRegions() {
        PostgreSQLQuery qry = queryFactory.from(qRegion);

        ComparableExpression<String> sortField = qRegion.nameFi.lower();
        qry.orderBy(sortField.asc());

        return qry.list(regionMapping);
    }

    @Override
    @TransactionalRead
    public Collection<RegionWithHubs> regionsWithHubs() {
        final Collection<Region> regions = getRegions();

        // We need two queries since dwithin did not seem to work in left join
        // at least on H2
        final Map<Long, Set<Long>> hubIdsByRegionIds = queryFactory.from(qRegion)
                .join(qHub)
                .on(dwithin(qRegion.area, qHub.location, 0.0))
                .transform(groupBy(qRegion.id).as(set(qHub.id)));

        return regions.stream()
                .map(region -> new RegionWithHubs(region, hubIdsByRegionIds.getOrDefault(region.id, new HashSet<>())))
                .collect(toList());
    }
}
