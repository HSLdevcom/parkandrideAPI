// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service.reporting;

import fi.hsl.parkandride.back.RegionRepository;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.*;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Set;

import static com.google.common.collect.Iterators.filter;
import static fi.hsl.parkandride.core.domain.Permission.REPORT_GENERATE;
import static fi.hsl.parkandride.core.service.AuthenticationService.authorize;
import static fi.hsl.parkandride.core.service.AuthenticationService.getLimitedOperatorId;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;
import static org.springframework.util.CollectionUtils.isEmpty;

public abstract class AbstractReportService extends ReportServiceSupport implements ReportService {
    private final String reportName;

    @Inject
    ExcelUtil excelUtil;

    protected AbstractReportService(String reportName, FacilityService facilityService, OperatorService operatorService, ContactService contactService, HubService hubService, UtilizationRepository utilizationRepository, TranslationService translationService, RegionRepository regionRepository) {
        super(facilityService, operatorService, contactService, hubService, utilizationRepository, translationService, regionRepository);
        this.reportName = reportName;
    }

    /**
     * Generates the Excel report and converts it to bytes.
     * Verifies that the current user has permission to generate reports.
     * Do not override.
     */
    @Override
    @TransactionalRead
    public byte[] generateReport(User currentUser, ReportParameters reportParameters) {
        authorize(currentUser, REPORT_GENERATE);
        ReportContext ctx = new ReportContext(this, getLimitedOperatorId(currentUser));
        return generateReport(ctx, reportParameters).toBytes();
    }

    /**
     * Get the name of the report this service produces.
     */
    @Override
    public String reportName() {
        return reportName;
    }

    protected abstract Excel generateReport(ReportContext reportContext, ReportParameters params);


    protected final UtilizationSearch toUtilizationSearch(ReportParameters parameters, final ReportContext ctx) {
        UtilizationSearch search = new UtilizationSearch();
        search.start = FINNISH_DATE_FORMAT.parseLocalDate(parameters.startDate).toDateTimeAtStartOfDay();
        if (parameters.endDate == null) {
            search.end = new LocalDate().plusDays(1).toDateTimeAtStartOfDay();
        } else {
            search.end = FINNISH_DATE_FORMAT.parseLocalDate(parameters.endDate).toDateTimeAtStartOfDay();
        }
        if (!isEmpty(parameters.capacityTypes)) {
            search.capacityTypes = parameters.capacityTypes;
        }
        if (!isEmpty(parameters.usages)) {
            search.usages = parameters.usages;
        }
        boolean emptyResults = false;
        if (!isEmpty(parameters.hubs)) {
            Set<Long> facilityIds = parameters.hubs.stream().map(hubId -> ctx.facilitiesByHubId.getOrDefault(hubId, emptyList())).flatMap(ids -> ids.stream()).map(f -> f.id).collect(toSet());
            emptyResults |= facilityIds.isEmpty();
            search.facilityIds.addAll(facilityIds);
        }
        if (!isEmpty(parameters.operators)) {
            Set<Long> facilityIds = parameters.operators.stream().map(oprId -> ctx.facilityIdsByOperatorId.getOrDefault(oprId, emptyList())).flatMap(ids -> ids.stream()).collect(toSet());
            emptyResults |= facilityIds.isEmpty();
            search.facilityIds.addAll(facilityIds);
        }
        if (emptyResults) {
            search.facilityIds.clear();
            search.facilityIds.add(-1L);
        } else if (ctx.allowedOperatorId != null) {
            if (isEmpty(parameters.facilities)) {
                // only fetch facilities that the user is allowed to see
                search.facilityIds = ctx.facilities.keySet();
            } else {
                // remove all facilities from search that the user is not allowed to see
                parameters.facilities.retainAll(ctx.facilities.keySet());
            }
        }
        return search;
    }

    protected Iterator<Utilization> addFilters(Iterator<Utilization> iter, ReportContext ctx, ReportParameters parameters) {
        if (ctx.allowedOperatorId != null) {
            iter = filter(iter, u -> ctx.facilities.containsKey(u.facilityId));
        }
        if (!isEmpty(parameters.operators)) {
            iter = filter(iter, u -> parameters.operators.contains(ctx.facilities.get(u.facilityId).operatorId));
        }
        if (!isEmpty(parameters.hubs)) {
            iter = filter(iter, u -> ctx.hubsByFacilityId.getOrDefault(u.facilityId, emptyList()).stream().filter(h -> parameters.hubs.contains(h.id)).findFirst().isPresent());
        }
        if (!isEmpty(parameters.regions)) {
            iter = filter(iter, u -> parameters.regions.contains(ctx.regionByFacilityId.get(u.facilityId).id));
        }
        if (!isEmpty(parameters.facilities)) {
            iter = filter(iter, u -> parameters.facilities.contains(u.facilityId));
        }
        return iter;
    }

    static class BasicUtilizationReportKey {
        CapacityType capacityType;
        Usage usage;
        Long targetId;

        public BasicUtilizationReportKey() {
        }

        public BasicUtilizationReportKey(Utilization u) {
            capacityType = u.capacityType;
            usage = u.usage;
            targetId = u.facilityId;
        }

        @Override
        public int hashCode() {
            return capacityType.hashCode() ^ targetId.hashCode() ^ usage.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            BasicUtilizationReportKey other = (BasicUtilizationReportKey) obj;
            if (capacityType != other.capacityType)
                return false;
            if (usage != other.usage)
                return false;
            if (!targetId.equals(other.targetId))
                return false;
            return true;
        }
    }

}
