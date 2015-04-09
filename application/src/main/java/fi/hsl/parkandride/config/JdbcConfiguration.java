// Copyright © 2015 HSL

package fi.hsl.parkandride.config;

import com.mysema.query.sql.SQLExceptionTranslator;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.sql.spatial.GeoDBTemplates;
import com.mysema.query.sql.spatial.PostGISTemplates;
import com.mysema.query.sql.types.DateTimeType;
import com.mysema.query.sql.types.EnumByNameType;
import fi.hsl.parkandride.FeatureProfile;
import fi.hsl.parkandride.back.H2GeometryType;
import fi.hsl.parkandride.back.LiipiSQLExceptionTranslator;
import fi.hsl.parkandride.back.PGGeometryType;
import fi.hsl.parkandride.back.TimeType;
import fi.hsl.parkandride.core.back.PhoneType;
import fi.hsl.parkandride.core.domain.*;
import org.geolatte.geom.Point;
import org.geolatte.geom.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
public class JdbcConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JdbcConfiguration.class);

    @Configuration
    @Profile({FeatureProfile.H2})
    public static class H2 {

        public H2() {
            log.info("USING H2");
        }

        @Bean
        public SQLTemplates sqlTemplates() {
            return new GeoDBTemplates();
        }

    }

    @Configuration
    @Profile(FeatureProfile.PSQL)
    public static class Postgresql {

        public Postgresql() {
            log.info("USING H2");
        }

        @Bean
        public SQLTemplates sqlTemplates() {
            return new PostGISTemplates();
        }
    }

    @Inject
    SQLTemplates sqlTemplates;

    @Inject
    DataSource dataSource;

    @Bean
    public PostgresQueryFactory queryFactory(com.mysema.query.sql.Configuration configuration) {
        return new PostgresQueryFactory(configuration, connectionProvider());
    }

    @Bean
    public Provider<Connection> connectionProvider() {
        return () -> {
            Connection conn = DataSourceUtils.getConnection(dataSource);
            if (!DataSourceUtils.isConnectionTransactional(conn, dataSource)) {
                throw new RuntimeException("Connection should be transactional");
            }
            return conn;
        };
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @Profile(FeatureProfile.PSQL)
    public com.mysema.query.sql.Configuration querydslConfigurationPsql() {
        com.mysema.query.sql.Configuration conf = querydslConfiguration();
        conf.register("FACILITY", "LOCATION", new PGGeometryType<>(Polygon.class));
        conf.register("PORT", "LOCATION", new PGGeometryType<>(Point.class));
        conf.register("HUB", "LOCATION", new PGGeometryType<>(Point.class));
        return conf;
    }

    @Bean
    @Profile({FeatureProfile.H2})
    public com.mysema.query.sql.Configuration querydslConfigurationH2() {
        com.mysema.query.sql.Configuration conf = querydslConfiguration();
        conf.register("FACILITY", "LOCATION", new H2GeometryType<>(Polygon.class));
        conf.register("PORT", "LOCATION", new H2GeometryType<>(Point.class));
        conf.register("HUB", "LOCATION", new H2GeometryType<>(Point.class));
        return conf;
    }

    private com.mysema.query.sql.Configuration querydslConfiguration() {
        com.mysema.query.sql.Configuration conf = new com.mysema.query.sql.Configuration(sqlTemplates);
        conf.setExceptionTranslator(sqlExceptionTranslator());

        conf.register(new TimeType());

        conf.register("PRICING", "CAPACITY_TYPE", new EnumByNameType<>(CapacityType.class));
        conf.register("PRICING", "USAGE", new EnumByNameType<>(Usage.class));
        conf.register("PRICING", "DAY_TYPE", new EnumByNameType<>(DayType.class));
        conf.register("PRICING", "FROM_TIME", new TimeType());
        conf.register("PRICING", "UNTIL_TIME", new TimeType());

        conf.register("UNAVAILABLE_CAPACITY", "CAPACITY_TYPE", new EnumByNameType<>(CapacityType.class));
        conf.register("UNAVAILABLE_CAPACITY", "USAGE", new EnumByNameType<>(Usage.class));

        conf.register("CAPACITY_TYPE", "NAME", new EnumByNameType<>(CapacityType.class));

        conf.register("CONTACT", "PHONE", new PhoneType());

        conf.register("APP_USER", "ROLE", new EnumByNameType<>(Role.class));

        conf.register("FACILITY", "STATUS", new EnumByNameType<>(FacilityStatus.class));

        conf.register("FACILITY", "PRICING_METHOD", new EnumByNameType<>(PricingMethod.class));

        conf.register("FACILITY_UTILIZATION", "CAPACITY_TYPE", new EnumByNameType<>(CapacityType.class));
        conf.register("FACILITY_UTILIZATION", "USAGE", new EnumByNameType<>(Usage.class));

        conf.register("FACILITY_PREDICTION", "CAPACITY_TYPE", new EnumByNameType<>(CapacityType.class));
        conf.register("FACILITY_PREDICTION", "USAGE", new EnumByNameType<>(Usage.class));

        conf.register("FACILITY_SERVICE", "SERVICE", new EnumByNameType<>(Service.class));

        conf.register("FACILITY_PAYMENT_METHOD", "PAYMENT_METHOD", new EnumByNameType<>(PaymentMethod.class));

        conf.register("PREDICTOR", "CAPACITY_TYPE", new EnumByNameType<>(CapacityType.class));
        conf.register("PREDICTOR", "USAGE", new EnumByNameType<>(Usage.class));

        conf.register(new DateTimeType());
        return conf;
    }

    private SQLExceptionTranslator sqlExceptionTranslator() {
        return new LiipiSQLExceptionTranslator();
    }
}
