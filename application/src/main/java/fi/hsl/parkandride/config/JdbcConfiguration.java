package fi.hsl.parkandride.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.inject.Provider;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.h2.Driver;
import org.springframework.context.annotation.Bean;
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
public class JdbcConfiguration {

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
        ds.setUsername("sa");
        ds.setPassword("");
        ds.setJdbcUrl("jdbc:h2:mem:liipi;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");

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
        flyway.setLocations("classpath:db/common", "classpath:db/h2");
        flyway.migrate();
    }

    @Bean
    public Configuration querydslConfiguration() {
        Configuration conf = new Configuration(sqlTemplates());
        conf.register("CAPACITY", "CAPACITY_TYPE", new EnumByNameType<CapacityType>(CapacityType.class));
//        conf.register("FACILITY", "BORDER", H2PolygonType.DEFAULT);
        return conf;
    }

    @Bean
    public SQLTemplates sqlTemplates() {
        // TODO: use PostGISTemplates for Postgresql
        return new H2GISTemplates();
    }

}
