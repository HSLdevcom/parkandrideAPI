package fi.hsl.parkandride.config;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.h2.Driver;
import org.springframework.context.annotation.Bean;

import com.mysema.query.sql.Configuration;

import com.mysema.query.sql.PostgresTemplates;
import com.mysema.query.sql.SQLTemplates;
import com.zaxxer.hikari.HikariDataSource;

@org.springframework.context.annotation.Configuration
public class JdbcConfiguration {

    @Bean
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName(Driver.class.getName());
        ds.setUsername("sa");
        ds.setPassword("");
        ds.setJdbcUrl("jdbc:h2:mem:liipi;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");

        return ds;
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
