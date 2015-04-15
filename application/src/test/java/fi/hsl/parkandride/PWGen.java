// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.mysema.query.sql.Configuration;
import com.mysema.query.sql.codegen.DefaultNamingStrategy;
import com.mysema.query.sql.codegen.MetaDataExporter;

import fi.hsl.parkandride.config.CoreConfiguration;
import fi.hsl.parkandride.config.JdbcConfiguration;
import fi.hsl.parkandride.core.service.AuthenticationService;

@org.springframework.context.annotation.Configuration
@EnableAutoConfiguration
@Import(CoreConfiguration.class)
@Profile("pwgen")
public class PWGen {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(PWGen.class);
        application.setWebEnvironment(false);
        application.setAdditionalProfiles("pwgen");
        application.run(args);
    }

    @Inject
    AuthenticationService authenticationService;

    @Bean
    public CommandLineRunner runner() {
        return (String... args) -> {
            if (args.length != 0) {
                String pwd = args[0];
                System.out.println(format("Password: %s -> %s", pwd, authenticationService.encryptPassword(pwd)));
            } else {
                throw new RuntimeException("Expected one argument: password to be encrypted.");
            }
        };
    }

}
