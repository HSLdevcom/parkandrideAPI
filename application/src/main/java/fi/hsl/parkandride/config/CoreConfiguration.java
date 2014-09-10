package fi.hsl.parkandride.config;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.mysema.query.sql.postgres.PostgresQueryFactory;

@Configuration
@Import(JdbcConfiguration.class)
public class CoreConfiguration {

    @Inject PostgresQueryFactory queryFactory;

}
