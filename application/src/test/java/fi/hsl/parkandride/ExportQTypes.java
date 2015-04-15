// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;

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

import fi.hsl.parkandride.config.JdbcConfiguration;

/**
 * NOTES:
 * - expected to be run from parkandride-application module
 */
@org.springframework.context.annotation.Configuration
@EnableAutoConfiguration
@Import(JdbcConfiguration.class)
@Profile(FeatureProfile.EXPORT_QTYPES)
public class ExportQTypes {

    public static final String NAME_PREFIX = "Q";
    public static final String TARGET_FOLDER = "src/generated/java";
    public static final String PACKAGE_NAME = "fi.hsl.parkandride.back.sql";

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ExportQTypes.class);
        application.setWebEnvironment(false);
        application.setAdditionalProfiles("export_qtypes");
        application.run(args);
    }

    @Inject
    DataSource dataSource;

    @Inject
    Configuration configuration;

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            MetaDataExporter exporter = new MetaDataExporter();
            exporter.setPackageName(PACKAGE_NAME);
            exporter.setInnerClassesForKeys(false);
            exporter.setSpatial(true);
            exporter.setNamePrefix(NAME_PREFIX);
            exporter.setNamingStrategy(new DefaultNamingStrategy());
            exporter.setTargetFolder(new File(TARGET_FOLDER));
            exporter.setConfiguration(configuration);
            Path packageDir = FileSystems.getDefault().getPath(TARGET_FOLDER, PACKAGE_NAME.replace(".", "/"));
            try (Connection conn = dataSource.getConnection()) {
                deleteOldQTypes(packageDir);
                exporter.export(conn.getMetaData());
                deleteUnusedQTypes(packageDir,
                        "QGeometryColumns.java",
                        "QSchemaVersion.java",
                        "QSpatialRefSys.java");
            }
            System.out.println("Export done");
        };
    }

    private static void deleteOldQTypes(Path packageDir) throws IOException {
        Files.walkFileTree(packageDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                if (path.getFileName().toString().startsWith(NAME_PREFIX)) {
                    Files.delete(path);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void deleteUnusedQTypes(Path packageDir, String... filesToRemove) throws IOException {
        for (String filename : filesToRemove) {
            Files.delete(packageDir.resolve(filename));
        }
    }
}
