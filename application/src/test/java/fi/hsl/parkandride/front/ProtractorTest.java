package fi.hsl.parkandride.front;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import fi.hsl.parkandride.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class ProtractorTest {

    @Value("${local.server.port}")
    private int port;

    @Test
    public void protractor_tests() throws InterruptedException, IOException {
        File projectDir = new File(System.getProperty("user.dir"));
        if (projectDir.getName().equals("application")) {
            projectDir = projectDir.getParentFile();
        }
        File script = new File(projectDir, "etc/protractor/protractor.sh");
        ProcessBuilder builder = new ProcessBuilder("bash", script.getAbsolutePath(),
                "test",
                "--browser=chrome",
                "--baseUrl=http://localhost:" + port)
                .inheritIO();
        Process process = builder.start();
        if (process.waitFor() != 0) {
            Assert.fail("Protractor tests failed");
        }
    }

}
