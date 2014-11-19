package fi.hsl.parkandride.config;

import java.sql.Connection;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;

import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.sql.spatial.PostGISTemplates;
import com.mysema.query.sql.types.EnumByNameType;
import com.zaxxer.hikari.HikariDataSource;

import fi.hsl.parkandride.core.domain.CapacityType;

@Configuration
@Import({
        PropertyPlaceholderAutoConfiguration.class,
        JdbcConfiguration.H2.class
    })
public class JdbcConfiguration {

    @Configuration
    @Profile({"!psql"})
    public static class H2 {

        public H2() {
            System.out.println("USING H2");
        }

        @Bean
        public String[] flywayLocations() {
            return new String[] { "classpath:db/common", "classpath:db/h2" };
        }

        @Bean
        public SQLTemplates sqlTemplates() {
            // TODO: use PostGISTemplates for Postgresql
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
        public String[] flywayLocations() {
            return new String[] { "classpath:db/common" };
        }

        @Bean
        public SQLTemplates sqlTemplates() {
            // TODO: use PostGISTemplates for Postgresql
            return new PostGISTemplates();
        }
    }

    @Value("${jdbc.username}")
    String username;

    @Value("${jdbc.password}")
    String password;

    @Value("${jdbc.url}")
    String url;

    @Value("${jdbc.driver}")
    String driverClassName;

    @Resource String[] flywayLocations;

    @Inject SQLTemplates sqlTemplates;

    @Bean
    public PostgresQueryFactory queryFactory() {
        return new PostgresQueryFactory(querydslConfiguration(), connectionProvider());
    }

    @Bean
    public Provider<Connection> connectionProvider() {
        final DataSource dataSource = dataSource();
        return new Provider<Connection>() {
            @Override
            public Connection get() {
                Connection conn = DataSourceUtils.getConnection(dataSource);
                if (!DataSourceUtils.isConnectionTransactional(conn, dataSource)) {
                    throw new RuntimeException("Connection should be transactional");
                }
                return conn;
            }
        };
    }

    @Bean
    public DataSource dataSource() {
        try {
            HikariDataSource ds = new HikariDataSource();
            ds.setDriverClassName(driverClassName);
            ds.setUsername(username);
            ds.setPassword(password);
            ds.setJdbcUrl(url);
            return ds;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @PostConstruct
    public void dbMigration() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource());
        flyway.setLocations(flywayLocations);
        flyway.migrate();
    }

    @Bean
    public com.mysema.query.sql.Configuration querydslConfiguration() {
        com.mysema.query.sql.Configuration conf = new com.mysema.query.sql.Configuration(sqlTemplates);
        conf.register("CAPACITY", "CAPACITY_TYPE", new EnumByNameType<CapacityType>(CapacityType.class));
        conf.register("CONTACT", "PHONE", new PhoneType());
//        conf.register("FACILITY", "BORDER", H2PolygonType.DEFAULT);
        return conf;
    }

}
