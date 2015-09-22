// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.back.RegionRepository;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import org.joda.time.format.DateTimeFormatter;

import static org.joda.time.format.DateTimeFormat.forPattern;

public abstract class ReportServiceSupport {

    public static final int SECONDS_IN_DAY = 60 * 60 * 24;
    public static final String FINNISH_DATE_PATTERN = "d.M.yyyy";
    public static final DateTimeFormatter FINNISH_DATE_FORMAT = forPattern(FINNISH_DATE_PATTERN);

    final FacilityService facilityService;
    final OperatorService operatorService;
    final ContactService contactService;
    final HubService hubService;
    final UtilizationRepository utilizationRepository;
    final TranslationService translationService;
    final RegionRepository regionRepository;

    protected ReportServiceSupport(FacilityService facilityService, OperatorService operatorService, ContactService contactService, HubService hubService, UtilizationRepository utilizationRepository, TranslationService translationService, RegionRepository regionRepository) {
        this.facilityService = facilityService;
        this.operatorService = operatorService;
        this.contactService = contactService;
        this.hubService = hubService;
        this.utilizationRepository = utilizationRepository;
        this.translationService = translationService;
        this.regionRepository = regionRepository;
    }
}
