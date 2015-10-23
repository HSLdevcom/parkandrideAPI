// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service.reporting;

import fi.hsl.parkandride.back.RegionRepository;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.service.*;

public abstract class ReportServiceSupport {

    public static final int SECONDS_IN_DAY = 60 * 60 * 24;

    final FacilityService facilityService;
    final OperatorService operatorService;
    final ContactService contactService;
    final HubService hubService;
    final UtilizationRepository utilizationRepository;
    final TranslationService translationService;
    final RegionRepository regionRepository;
    final FacilityHistoryService facilityHistoryService;

    protected ReportServiceSupport(FacilityService facilityService, OperatorService operatorService, ContactService contactService, HubService hubService, UtilizationRepository utilizationRepository, TranslationService translationService, RegionRepository regionRepository, FacilityHistoryService facilityHistoryService) {
        this.facilityService = facilityService;
        this.operatorService = operatorService;
        this.contactService = contactService;
        this.hubService = hubService;
        this.utilizationRepository = utilizationRepository;
        this.translationService = translationService;
        this.regionRepository = regionRepository;
        this.facilityHistoryService = facilityHistoryService;
    }
}
