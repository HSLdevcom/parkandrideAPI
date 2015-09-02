// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride;

import static fi.hsl.parkandride.front.UrlSchema.GEOJSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.List;
import java.util.Properties;

import org.geolatte.common.Feature;
import org.geolatte.common.dataformats.json.jackson.JsonMapper;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.Point;
import org.geolatte.geom.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.MimeMappings;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.nitorcreations.willow.logging.tomcat8.WillowAccessValve;

import fi.hsl.parkandride.core.domain.Phone;
import fi.hsl.parkandride.core.domain.Time;
import fi.hsl.parkandride.front.CORSFilter;
import fi.hsl.parkandride.front.Features;
import fi.hsl.parkandride.front.PhoneSerializer;
import fi.hsl.parkandride.front.UrlSchema;
import fi.hsl.parkandride.front.UserArgumentResolver;
import fi.hsl.parkandride.front.geojson.GeojsonDeserializer;
import fi.hsl.parkandride.front.geojson.GeojsonSerializer;

@SpringBootApplication
@Import(Application.UiConfig.class)
public class Application extends WebMvcConfigurerAdapter {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.addListeners(new ApplicationPidListener());
        app.run(args);
    }

    @Configuration
    @Import({WebMvcAutoConfiguration.class, DevUIConfig.class})
    public static class UiConfig extends WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter implements EmbeddedServletContainerCustomizer {

        @Autowired
        private HttpMessageConverters messageConverters;

        @Bean
        public Module facilityModule() {
            return new SimpleModule("geometryModule") {{
                final JsonMapper jsonMapper = new JsonMapper();

                addSerializer(Geometry.class, new GeojsonSerializer<>(jsonMapper));
                addDeserializer(Geometry.class, new GeojsonDeserializer<>(jsonMapper, Geometry.class));

                addSerializer(Polygon.class, new GeojsonSerializer<>(jsonMapper));
                addDeserializer(Polygon.class, new GeojsonDeserializer<>(jsonMapper, Polygon.class));

                addSerializer(Point.class, new GeojsonSerializer<>(jsonMapper));
                addDeserializer(Point.class, new GeojsonDeserializer<>(jsonMapper, Point.class));

                addSerializer(Feature.class, new GeojsonSerializer<>(jsonMapper));
                addSerializer(Phone.class, new PhoneSerializer());

                addSerializer(Time.class, new ToStringSerializer());
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
            configurer.defaultContentType(APPLICATION_JSON);
            configurer.mediaType("json", APPLICATION_JSON);
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

        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
            argumentResolvers.add(userArgumentResolver());
        }

        @Bean
        public UserArgumentResolver userArgumentResolver() {
            return new UserArgumentResolver();
        }

        @Override
        public void customize(ConfigurableEmbeddedServletContainer container) {
            MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
            mappings.add("html", "text/html;charset=UTF-8");
            mappings.add("json", "application/json;charset=UTF-8");
            container.setMimeMappings(mappings);
            if (System.getProperty("wslogging.url") != null &&
                  container instanceof TomcatEmbeddedServletContainerFactory) {
                TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory)container;
                WillowAccessValve valve = new WillowAccessValve();
                valve.setUrl(System.getProperty("wslogging.url"));
                tomcat.addContextValves(valve);
            }
        }
    }

    @Configuration
    @EnableWebMvc
    @Profile({FeatureProfile.DEV})
    public static class DevUIConfig extends WebMvcConfigurerAdapter {

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            String projectDir = System.getProperty("user.dir");
            if (!projectDir.endsWith("application")) {
                projectDir += "/application";
            }

            registry.addResourceHandler("/**").addResourceLocations(
                    "file://" + projectDir + "/src/main/frontend/build/",
                    "/",
                    "classpath:/META-INF/resources/",
                    "classpath:/resources/",
                    "classpath:/static/",
                    "classpath:/public/"
            );
        }

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/").setViewName("forward:/index.html");
        }
    }

    @Bean
    public FilterRegistrationBean registerMdcFilter(MDCFilter filter) {
        FilterRegistrationBean b = new FilterRegistrationBean(filter);
        b.setMatchAfter(true);
        return b;
    }

    @Bean
    public FilterRegistrationBean registerCORSFilter() {
        FilterRegistrationBean b = new FilterRegistrationBean(new CORSFilter());
        b.setMatchAfter(true);
        b.setUrlPatterns(UrlSchema.CORS_ENABLED_PATHS);
        return b;
    }

    @Bean
    public FilterRegistrationBean registerApplicationVersionFilter(@Value("${app.version}") String version) {
        log.info("Application version is {}", version);
        FilterRegistrationBean b = new FilterRegistrationBean(new ApplicationVersionFilter(version));
        b.setMatchAfter(true);
        return b;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebContentInterceptor webContentInterceptor = new WebContentInterceptor();
        Properties cacheMappings = new Properties();
        cacheMappings.setProperty(UrlSchema.API + "/**", "0");
        webContentInterceptor.setCacheMappings(cacheMappings);
        registry.addInterceptor(webContentInterceptor);
    }
}
