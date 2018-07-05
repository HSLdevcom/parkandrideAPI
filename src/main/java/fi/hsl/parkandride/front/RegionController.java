// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.front;

import fi.hsl.parkandride.back.RegionRepository;
import fi.hsl.parkandride.core.domain.Region;
import fi.hsl.parkandride.core.domain.RegionWithHubs;
import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.service.HubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Collection;

import static fi.hsl.parkandride.core.domain.Permission.REPORT_GENERATE;
import static fi.hsl.parkandride.core.service.AuthenticationService.authorize;
import static fi.hsl.parkandride.front.UrlSchema.REGIONS;
import static fi.hsl.parkandride.front.UrlSchema.REGIONS_WITH_HUBS;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class RegionController {
    private static final Logger log = LoggerFactory.getLogger(RegionController.class);

    @Inject
    RegionRepository regionRepository;

    @Inject
    HubService hubService;

    @RequestMapping(method = GET, value = REGIONS)
    public Collection<Region> regions() {
        log.info("regions()");
        return regionRepository.getRegions();
    }

    @RequestMapping(method = GET, value = REGIONS_WITH_HUBS)
    public Collection<RegionWithHubs> regionsWithHubs(User currentUser) {
        // This is here to trigger authentication when moving to reporting page.
        // Maybe be more explicit?
        authorize(currentUser, REPORT_GENERATE);
        log.info("regionsWithHubs()");
        return regionRepository.regionsWithHubs();
    }
}
