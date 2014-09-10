package fi.hsl.parkandride.outbound;

import java.security.cert.PolicyQualifierInfo;

import com.mysema.query.sql.postgres.PostgresQueryFactory;

import fi.hsl.parkandride.core.outbound.FacilityRepository;

public class FacilityDao implements FacilityRepository {

    private final PostgresQueryFactory queryFactory;

    public FacilityDao(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

}
