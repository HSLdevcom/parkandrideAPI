package fi.hsl.parkandride;

import static fi.hsl.parkandride.front.UrlSchema.GEOJSON;

import java.io.IOException;
import java.util.List;

import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.geolatte.common.Feature;
import org.geolatte.common.dataformats.json.jackson.JsonMapper;
import org.geolatte.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidListener;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;

import fi.hsl.parkandride.front.Features;
import fi.hsl.parkandride.front.GeojsonDeserializer;
import fi.hsl.parkandride.front.GeojsonSerializer;

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

        @Autowired
        private HttpMessageConverters messageConverters;

        @Bean
        public Module facilityModule() {
            return new SimpleModule("geometryModule") {{
                final JsonMapper jsonMapper = new JsonMapper();

                addSerializer(Geometry.class, new GeojsonSerializer<>(jsonMapper));
                addDeserializer(Geometry.class, new GeojsonDeserializer<>(jsonMapper, Geometry.class));

                addSerializer(Feature.class, new GeojsonSerializer<>(jsonMapper));
            }};
        }

        @Bean
        public CharacterEncodingFilter characterEncodingFilter() {
            CharacterEncodingFilter filter = new CharacterEncodingFilter();
            filter.setEncoding("UTF-8");
            filter.setForceEncoding(true);
            return filter;
        }

        @Bean
        public Features features() {
            return new Features();
        }

        @Override
        public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
            configurer.defaultContentType(MediaType.APPLICATION_JSON);
            configurer.mediaType("geojson", MediaType.valueOf(GEOJSON));
        }

        @Bean
        public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver() {
            ExceptionHandlerExceptionResolver resolver = new ExceptionHandlerExceptionResolver();
            resolver.setWarnLogCategory("parkandride");
            resolver.setMessageConverters(messageConverters.getConverters());
            return resolver;
        }

        @Override
        public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
            exceptionResolvers.add(exceptionHandlerExceptionResolver());
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