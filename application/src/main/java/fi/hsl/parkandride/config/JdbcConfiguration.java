package fi.hsl.parkandride.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.h2.Driver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;

import com.mysema.query.sql.Configuration;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.sql.types.EnumByNameType;
import com.zaxxer.hikari.HikariDataSource;

import fi.hsl.parkandride.core.domain.CapacityType;

@org.springframework.context.annotation.Configuration
@Import({
        PropertyPlaceholderAutoConfiguration.class,
        JdbcConfiguration.H2.class
    })
public class JdbcConfiguration {

    @org.springframework.context.annotation.Configuration
    @Profile("h2")
    public static class H2 {

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

    @Value("${jdbc.username}")
    String username;

    @Value("${jdbc.password}")
    String password;

    @Value("${jdbc.url}")
    String url;

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
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName(Driver.class.getName());
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setJdbcUrl(url);
        return ds;
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
    public Configuration querydslConfiguration() {
        Configuration conf = new Configuration(sqlTemplates);
        conf.register("CAPACITY", "CAPACITY_TYPE", new EnumByNameType<CapacityType>(CapacityType.class));
//        conf.register("FACILITY", "BORDER", H2PolygonType.DEFAULT);
        return conf;
    }

}
