package fi.hsl.parkandride;

import static fi.hsl.parkandride.front.UrlSchema.GEOJSON;

import java.util.List;

import org.apache.log4j.MDC;
import org.geolatte.common.Feature;
import org.geolatte.common.dataformats.json.jackson.JsonMapper;
import org.geolatte.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidListener;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.base.Preconditions;

import fi.hsl.parkandride.core.domain.Phone;
import fi.hsl.parkandride.front.Features;
import fi.hsl.parkandride.front.GeojsonDeserializer;
import fi.hsl.parkandride.front.GeojsonSerializer;
import fi.hsl.parkandride.front.PhoneSerializer;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@Import(Application.UiConfig.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.addListeners(new ApplicationPidListener());
        app.run(args);
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
                addSerializer(Phone.class, new PhoneSerializer());
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
    @EnableWebMvc
    @Profile({"dev"})
    public static class DevUIConfig extends WebMvcConfigurerAdapter {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            String projectDir = System.getProperty("user.dir");
            if (!projectDir.endsWith("application")) {
                projectDir += "/application";
            }
            registry.addResourceHandler("/**").addResourceLocations(
                    "file://" + projectDir + "/src/main/frontend/build/"
                    ,"/"
                    ,"classpath:/META-INF/resources/"
                    ,"classpath:/resources/"
                    ,"classpath:/static/"
                    ,"classpath:/public/"
            );
        }

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/").setViewName("forward:/index.html");
        }
    }

    @Bean
    public FilterRegistrationBean registerMdcFilter(MdcFilter filter) {
        Preconditions.checkNotNull(filter);
        FilterRegistrationBean b = new FilterRegistrationBean();
        b.setFilter(filter);
        b.setMatchAfter(true);
        return b;
    }
}