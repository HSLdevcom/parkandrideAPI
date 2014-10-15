package fi.hsl.parkandride;

import static fi.hsl.parkandride.inbound.UrlSchema.GEOJSON;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

import java.io.IOException;
import java.util.List;

import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.geolatte.common.Feature;
import org.geolatte.common.dataformats.json.jackson.FeatureSerializer;
import org.geolatte.common.dataformats.json.jackson.JsonMapper;
import org.geolatte.geom.Geometry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidListener;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;

import fi.hsl.parkandride.inbound.GeojsonDeserializer;
import fi.hsl.parkandride.inbound.GeojsonSerializer;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@Import(Application.UiConfig.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.addListeners(new ApplicationPidListener());
        app.run(args);
//        SpringApplication.run(Application.class, args);
    }

    @Configuration
    @Import({ WebMvcAutoConfiguration.class, DevUIConfig.class })
    public static class UiConfig extends WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter {

        @Override
        public void addResourceHandlers(final ResourceHandlerRegistry registry) {
            super.addResourceHandlers(registry);
        }

        @Bean
        public Module facilityModule() {
            return new SimpleModule("geometryModule") {{
                final JsonMapper jsonMapper = new JsonMapper();

                addSerializer(Geometry.class, new GeojsonSerializer<>(jsonMapper));
                addDeserializer(Geometry.class, new GeojsonDeserializer<>(jsonMapper, Geometry.class));

                addSerializer(Feature.class, new GeojsonSerializer<>(jsonMapper));
            }};
        }

        @Override
        public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
            configurer.defaultContentType(MediaType.APPLICATION_JSON);
            configurer.mediaType("geojson", MediaType.valueOf(GEOJSON));
        }

        @Bean
        public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver() {
            return new ExceptionHandlerExceptionResolver();
        }

        @Bean
        public DefaultHandlerExceptionResolver defaultHandlerExceptionResolver() {
            DefaultHandlerExceptionResolver resolver = new DefaultHandlerExceptionResolver();
            resolver.setWarnLogCategory("parkandride");
            return resolver;
        }

        @Override
        public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
            exceptionResolvers.add(exceptionHandlerExceptionResolver());
            exceptionResolvers.add(defaultHandlerExceptionResolver());
        }
    }

    @Configuration
    @Profile("dev")
    public static class DevUIConfig {

        @Bean
        public EmbeddedServletContainerFactory servletContainer() {
            return new JettyEmbeddedServletContainerFactory() {
                @Override
                protected void postProcessWebAppContext(WebAppContext webAppContext) {
                    Resource defaultResource = webAppContext.getBaseResource();

                    String projectDir = System.getProperty("user.dir");
                    if (!projectDir.endsWith("application")) {
                        projectDir += "/application";
                    }
                    try {
                        Resource devResource = Resource.newResource(projectDir + "/src/main/frontend/build");
                        webAppContext.setBaseResource(new ResourceCollection(devResource, defaultResource));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }
    }
}