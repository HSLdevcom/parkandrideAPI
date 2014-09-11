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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mysema.query.sql.Configuration;

import com.mysema.query.sql.PostgresTemplates;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.zaxxer.hikari.HikariDataSource;

@org.springframework.context.annotation.Configuration
@EnableTransactionManagement
public class JdbcConfiguration {

    @Bean
    public PostgresQueryFactory queryFactory() {
        return new PostgresQueryFactory(querydslConfiguration(), connectionProvider());
    }

    @Bean
    public Provider<Connection> connectionProvider() {
        final DataSource ds = dataSource();
        return new Provider<Connection>() {
            @Override
            public Connection get() {
                try {
                    // TODO: ensure connection is transactional!
                    return ds.getConnection();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
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
    public PlatformTransactionManager txManager() {
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
        return conf;
    }

    @Bean
    public SQLTemplates sqlTemplates() {
        return new PostgresTemplates();
    }

}
