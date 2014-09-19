package fi.hsl.parkandride;

import org.geolatte.common.dataformats.json.jackson.JsonMapper;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.crs.CrsId;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;

import fi.hsl.parkandride.inbound.GeometryDeserializer;
import fi.hsl.parkandride.inbound.GeometrySerializer;

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
        @Override
        public void addResourceHandlers(final ResourceHandlerRegistry registry) {
            super.addResourceHandlers(registry);
        }

        @Bean
        public Module facilityModule() {
            return new SimpleModule("geometryModule") {{
                final JsonMapper jsonMapper = new JsonMapper();
                addSerializer(Geometry.class, new GeometrySerializer(jsonMapper));
                addDeserializer(Geometry.class, new GeometryDeserializer(jsonMapper));
            }};
        }
    }
}