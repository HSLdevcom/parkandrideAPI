// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.back.RegionRepository;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.front.ReportParameters;
import org.apache.poi.ss.usermodel.CellStyle;
import org.joda.time.LocalDate;
import org.springframework.context.MessageSource;

import javax.inject.Inject;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

import static fi.hsl.parkandride.core.domain.Permission.REPORT_GENERATE;
import static fi.hsl.parkandride.core.service.AuthenticationService.authorize;
import static fi.hsl.parkandride.core.service.AuthenticationService.getLimitedOperatorId;
import static fi.hsl.parkandride.core.service.Excel.TableColumn.col;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;
import static org.springframework.util.CollectionUtils.isEmpty;

public abstract class AbstractReportService extends ReportServiceSupport implements ReportService {
    private static final Locale DEFAULT_LOCALE = new Locale("fi");
    private final String reportName;

    @Inject
    MessageSource messageSource;

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

    protected String getMessage(String code) {
        return messageSource.getMessage(code, null, DEFAULT_LOCALE);
    }

    /**
     * Get the name of the report this service produces.
     */
    @Override
    public String reportName() {
        return reportName;
    }

    protected abstract Excel generateReport(ReportContext reportContext, ReportParameters params);


    protected static String time(TimeDuration time) {
        return time == null ? null : format("%02d:%02d - %02d:%02d", time.from.getHour(), time.from.getMinute(), time.until.getHour(), time.until.getMinute());
    }

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

    /** Adds translated column */
    protected <T> Excel.TableColumn<T> tcol(String key, Function<T, Object> valFn) {
        return col(getMessage(key), valFn);
    }
    protected <T> Excel.TableColumn<T> tcol(String key, Function<T, Object> valFn, CellStyle cellStyle) {
        return col(getMessage(key), valFn, cellStyle);
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
