// Copyright © 2015 HSL

package fi.hsl.parkandride.back;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hsl.parkandride.DevApiProfileAppender;
import fi.hsl.parkandride.dev.DevHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
@ActiveProfiles(resolver = DevApiProfileAppender.class)
public abstract class AbstractDaoTest {
    @Inject
    protected DevHelper devHelper;

    @Before
    public void cleanup() {
        devHelper.deleteAll();
    }

}
