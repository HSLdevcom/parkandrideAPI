package fi.hsl.parkandride.config;

import java.sql.Connection;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;

import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.sql.spatial.PostGISTemplates;
import com.mysema.query.sql.types.DateTimeType;
import com.mysema.query.sql.types.EnumByNameType;

import fi.hsl.parkandride.core.domain.CapacityType;
import fi.hsl.parkandride.core.domain.FacilityStatusEnum;
import fi.hsl.parkandride.core.domain.Role;

@Configuration
public class JdbcConfiguration {

    @Configuration
    @Profile({"!psql"})
    public static class H2 {

        public H2() {
            System.out.println("USING H2");
        }

        @Bean
        public SQLTemplates sqlTemplates() {
            return new H2GISTemplates();
        }

    }

    @Configuration
    @Profile("psql")
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
    public com.mysema.query.sql.Configuration querydslConfiguration() {
        com.mysema.query.sql.Configuration conf = new com.mysema.query.sql.Configuration(sqlTemplates);
        conf.register("CAPACITY", "CAPACITY_TYPE", new EnumByNameType<>(CapacityType.class));
        conf.register("CAPACITY_TYPE", "NAME", new EnumByNameType<>(CapacityType.class));

        conf.register("CONTACT", "PHONE", new PhoneType());

        conf.register("APP_USER", "ROLE", new EnumByNameType<Role>(Role.class));
//        conf.register("FACILITY", "BORDER", H2PolygonType.DEFAULT);

        conf.register("FACILITY_STATUS", "CAPACITY_TYPE", new EnumByNameType<>(CapacityType.class));
        conf.register("FACILITY_STATUS", "STATUS", new EnumByNameType<>(FacilityStatusEnum.class));
        conf.register("FACILITY_STATUS_ENUM", "NAME", new EnumByNameType<>(FacilityStatusEnum.class));

        conf.register(new DateTimeType());
        return conf;
    }
}
