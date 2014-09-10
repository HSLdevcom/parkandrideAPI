package fi.hsl.parkandride.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(JdbcConfiguration.class)
public class CoreConfiguration {

}
