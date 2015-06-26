// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import fi.hsl.parkandride.DevApiProfileAppender;
import fi.hsl.parkandride.dev.DevHelper;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
@ActiveProfiles(resolver = DevApiProfileAppender.class)
public abstract class AbstractDaoTest {

    @Inject
    protected DevHelper devHelper;

    @Before
    public final void cleanup() {
        devHelper.deleteAll();
    }
}
