package fi.hsl.parkandride.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import fi.hsl.parkandride.adapter.rest.controller.ParkingAreaCommandController;

@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = { ParkingAreaCommandController.class })
public class RestConfig {
}
