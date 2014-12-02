package fi.hsl.parkandride.front;

import static java.util.Arrays.asList;
import static org.assertj.core.util.Strings.isNullOrEmpty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ActiveProfilesResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import fi.hsl.parkandride.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles(resolver = ProtractorTest.TestProfilesResolver.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class ProtractorTest {

    public static class TestProfilesResolver implements ActiveProfilesResolver {
        @Override
        public String[] resolve(Class<?> testClass) {
            String springProfilesActive = System.getProperty("spring.profiles.active");
            if (isNullOrEmpty(springProfilesActive)) {
                springProfilesActive = System.getenv("SPRING_PROFILES_ACTIVE");
            }
            List<String> profiles = new ArrayList<>();
            if (!isNullOrEmpty(springProfilesActive)) {
                profiles.addAll(asList(springProfilesActive.split(",")));
            }
            profiles.add("protractor");
            profiles.add("dev_api");
            System.out.println("profiles = " + profiles);
            return profiles.toArray(new String[profiles.size()]);
        }
    }

    @Value("${local.server.port}")
    private int port;

    @Value("${browser:firefox}")
    private String browser;

    @Test
    public void protractor_tests() throws InterruptedException, IOException {
        File projectDir = new File(System.getProperty("user.dir"));
        if (projectDir.getName().equals("application")) {
            projectDir = projectDir.getParentFile();
        }
        File script = new File(projectDir, "etc/protractor/protractor.sh");
        ProcessBuilder builder = new ProcessBuilder("bash", script.getAbsolutePath(),
                "test",
                "--browser=" + browser,
                "--baseUrl=http://localhost:" + port)
                .inheritIO();
        Process process = builder.start();
        if (process.waitFor() != 0) {
            Assert.fail("Protractor tests failed");
        }
    }

}
