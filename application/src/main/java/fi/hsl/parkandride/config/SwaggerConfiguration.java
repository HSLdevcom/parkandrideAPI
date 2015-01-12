package fi.hsl.parkandride.config;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.ImmutableList;
import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.models.dto.ApiKey;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;

import fi.hsl.parkandride.core.domain.Phone;
import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.front.swagger.Geometry;
import fi.hsl.parkandride.front.swagger.Point;
import fi.hsl.parkandride.front.swagger.Polygon;

@Configuration
@EnableSwagger
public class SwaggerConfiguration {

    @Inject SpringSwaggerConfig springSwaggerConfig;

    @Bean
    public SwaggerSpringMvcPlugin publicAPI() {
        return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
                .apiVersion("1.0")
                .swaggerGroup("api-v1")
                .directModelSubstitute(org.geolatte.geom.Geometry.class, Geometry.class)
                .directModelSubstitute(org.geolatte.geom.Polygon.class, Polygon.class)
                .directModelSubstitute(org.geolatte.geom.Point.class, Point.class)
                .directModelSubstitute(Phone.class, String.class)
                .ignoredParameterTypes(User.class)
                .authorizationTypes(ImmutableList.of(new ApiKey("Authorization", "header")))
                .apiInfo(publicApiInfo())
                .includePatterns("/api/v1/.*");
    }

    private ApiInfo publicApiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                "Park and Ride",
                "Discover available parking space near public transportation hubs.\n\n" +
                    "NOTE: GeoJSON Geometry models are broken until swagger-springmvc supports Swagger 2.0.",
                "TODO: Terms of service",
                "TODO: Contact Email",
                "TODO: Licence Type",
                "TODO: License URL"
        );
        return apiInfo;
    }
}
