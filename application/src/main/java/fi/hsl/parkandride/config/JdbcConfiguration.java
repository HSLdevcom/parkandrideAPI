package fi.hsl.parkandride.config;

import java.sql.Connection;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.sql.DataSource;

import org.geolatte.geom.Point;
import org.geolatte.geom.Polygon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;

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
import fi.hsl.parkandride.core.domain.CapacityType;
import fi.hsl.parkandride.core.domain.DayType;
import fi.hsl.parkandride.core.domain.FacilityStatusEnum;
import fi.hsl.parkandride.core.domain.Role;
import fi.hsl.parkandride.core.domain.Usage;

@Configuration
public class JdbcConfiguration {

    @Configuration
    @Profile({FeatureProfile.H2})
    public static class H2 {

        public H2() {
            System.out.println("USING H2");
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
            System.out.println("USING POSTGRESQL");
        }

        @Bean
        public SQLTemplates sqlTemplates() {
            return new PostGISTemplates();
        }
    }

    @Inject SQLTemplates sqlTemplates;

    @Inject DataSource dataSource;

    @Bean
    public PostgresQueryFactory queryFactory() {
        return new PostgresQueryFactory(querydslConfiguration(), connectionProvider());
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
        conf.register("FACILITY", "LOCATION", new PGGeometryType(Polygon.class));
        conf.register("PORT", "LOCATION", new PGGeometryType(Point.class));
        conf.register("HUB", "LOCATION", new PGGeometryType(Point.class));
        return conf;
    }

    @Bean
    @Profile({FeatureProfile.H2})
    public com.mysema.query.sql.Configuration querydslConfigurationH2() {
        com.mysema.query.sql.Configuration conf = querydslConfiguration();
        conf.register("FACILITY", "LOCATION", new H2GeometryType(Polygon.class));
        conf.register("PORT", "LOCATION", new H2GeometryType(Point.class));
        conf.register("HUB", "LOCATION", new H2GeometryType(Point.class));
        return conf;
    }

    private com.mysema.query.sql.Configuration querydslConfiguration() {
        com.mysema.query.sql.Configuration conf = new com.mysema.query.sql.Configuration(sqlTemplates);
        conf.setExceptionTranslator(sqlExceptionTranslator());

        conf.register("PRICING", "FROM_TIME", new TimeType());
        conf.register("PRICING", "UNTIL_TIME", new TimeType());
        conf.register("PRICING", "USAGE", new EnumByNameType<>(Usage.class));
        conf.register("PRICING", "CAPACITY_TYPE", new EnumByNameType<>(CapacityType.class));
        conf.register("PRICING", "DAY_TYPE", new EnumByNameType<>(DayType.class));

        conf.register("CAPACITY_TYPE", "NAME", new EnumByNameType<>(CapacityType.class));

        conf.register("CONTACT", "PHONE", new PhoneType());

        conf.register("APP_USER", "ROLE", new EnumByNameType<Role>(Role.class));

        conf.register("FACILITY_STATUS", "CAPACITY_TYPE", new EnumByNameType<>(CapacityType.class));
        conf.register("FACILITY_STATUS", "STATUS", new EnumByNameType<>(FacilityStatusEnum.class));
        conf.register("FACILITY_STATUS_ENUM", "NAME", new EnumByNameType<>(FacilityStatusEnum.class));

        conf.register(new DateTimeType());
        return conf;
    }

    private SQLExceptionTranslator sqlExceptionTranslator() {
        return new LiipiSQLExceptionTranslator();
    }
}
