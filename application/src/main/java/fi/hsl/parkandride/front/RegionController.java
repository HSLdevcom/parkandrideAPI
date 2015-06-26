// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.front;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import fi.hsl.parkandride.back.RegionRepository;
import fi.hsl.parkandride.core.domain.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.inject.Inject;
import java.util.Collection;
import static fi.hsl.parkandride.front.UrlSchema.REGIONS;

@RestController
public class RegionController {
    private static final Logger log = LoggerFactory.getLogger(RegionController.class);

    @Inject
    RegionRepository regionRepository;

    @RequestMapping(method = GET, value = REGIONS)
    public Collection<Region> regions() {
        log.info("regions()");
        return regionRepository.getRegions();
    }
}
