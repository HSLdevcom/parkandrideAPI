package fi.hsl.parkandride;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@Import(Application.UiConfig.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Configuration
    @Import(WebMvcAutoConfiguration.class)
    public static class UiConfig extends WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter {
        @Override public void addResourceHandlers(final ResourceHandlerRegistry registry) {
            super.addResourceHandlers(registry);
        }
    }
}
