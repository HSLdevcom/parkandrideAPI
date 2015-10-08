// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service.reporting;

import com.mysema.commons.lang.CloseableIterator;
import fi.hsl.parkandride.back.RegionRepository;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.Facility;
import fi.hsl.parkandride.core.domain.Hub;
import fi.hsl.parkandride.core.domain.Utilization;
import fi.hsl.parkandride.core.domain.UtilizationSearch;
import fi.hsl.parkandride.core.service.*;
import fi.hsl.parkandride.front.ReportParameters;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static fi.hsl.parkandride.core.domain.DayType.*;
import static fi.hsl.parkandride.util.ArgumentValidator.validate;
import static java.time.LocalTime.ofSecondOfDay;
import static java.util.Arrays.asList;
import static java.util.Arrays.fill;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class FacilityUsageReportService extends AbstractReportService {

    private static final String REPORT_NAME = "FacilityUsage";

    public FacilityUsageReportService(FacilityService facilityService, OperatorService operatorService, ContactService contactService, HubService hubService,
                                      UtilizationRepository utilizationRepository, RegionRepository regionRepository, TranslationService translationService) {
        super(REPORT_NAME, facilityService, operatorService, contactService, hubService, utilizationRepository, translationService, regionRepository);
    }

    @Override
    protected Excel generateReport(ReportContext ctx, ReportParameters parameters) {
        int intervalSeconds = validate(parameters.interval).gt(0) * 60;

        UtilizationSearch search = toUtilizationSearch(parameters, ctx);
        validate(search.start).lte(search.end);

        Map<UtilizationReportKey, UtilizationReportRow> reportRows = new LinkedHashMap<>();

        try (CloseableIterator<Utilization> utilizations = utilizationRepository.findUtilizations(search)) {
            addFilters(utilizations, ctx, parameters).forEachRemaining(u -> {
                UtilizationReportKey key = new UtilizationReportKey(u);
                key.facility = ctx.facilities.get(u.facilityId);
                UtilizationReportRow value = reportRows.get(key);
                if (value == null) {
                    UtilizationReportRow prevDayRow = reportRows.get(key.prevDay());
                    int initialValue = 0;
                    if (prevDayRow != null) {
                        initialValue = prevDayRow.values[prevDayRow.values.length - 1];
                    }
                    value = new UtilizationReportRow(key, intervalSeconds, initialValue);
                    reportRows.put(key, value);
                }
                value.setValue(u.timestamp, u.spacesAvailable);
            });
        }

        Excel excel = new Excel();

        List<UtilizationReportRow> rows = reportRows.values()
                .stream()
                .sorted(comparing((UtilizationReportRow row) -> row.key.date)
                        .thenComparing(row -> row.key.facility.name.fi)
                        .thenComparing(row -> row.key.capacityType)
                        .thenComparing(row -> row.key.usage)
                )
                .collect(toList());

        List<Excel.TableColumn<UtilizationReportRow>> columns = asList(
                excelUtil.tcol("reports.usage.col.facility", (UtilizationReportRow r) -> r.key.facility.name),
                excelUtil.tcol("reports.usage.col.hub", (UtilizationReportRow r) -> ctx.hubsByFacilityId.getOrDefault(r.key.targetId, emptyList()).stream().map((Hub h) -> h.name.fi).collect(joining(", "))),
                excelUtil.tcol("reports.usage.col.region", (UtilizationReportRow r) -> ctx.regionByFacilityId.get(r.key.targetId).name),
                excelUtil.tcol("reports.usage.col.operator", (UtilizationReportRow r) -> operatorService.getOperator(r.key.facility.operatorId).name),
                excelUtil.tcol("reports.usage.col.usage", (UtilizationReportRow r) -> translationService.translate(r.key.usage)),
                excelUtil.tcol("reports.usage.col.capacityType", (UtilizationReportRow r) -> translationService.translate(r.key.capacityType)),
                excelUtil.tcol("reports.usage.col.status", (UtilizationReportRow r) -> translationService.translate(r.key.facility.status)),
                excelUtil.tcol("reports.usage.col.openingHoursBusinessDay", (UtilizationReportRow r) -> ExcelUtil.time(r.key.facility.openingHours.byDayType.get(BUSINESS_DAY))),
                excelUtil.tcol("reports.usage.col.openingHoursSaturday", (UtilizationReportRow r) -> ExcelUtil.time(r.key.facility.openingHours.byDayType.get(SATURDAY))),
                excelUtil.tcol("reports.usage.col.openingHoursSunday", (UtilizationReportRow r) -> ExcelUtil.time(r.key.facility.openingHours.byDayType.get(SUNDAY))),
                excelUtil.tcol("reports.usage.col.spacesAvailable", (UtilizationReportRow r) -> r.key.facility.builtCapacity.get(r.key.capacityType)),
                excelUtil.tcol("reports.usage.col.date", (UtilizationReportRow r) -> r.key.date)
        );
        columns = new ArrayList<>(columns);
        for (int s = 0, i = 0; s < SECONDS_IN_DAY; s += intervalSeconds, i++) {
            final int idx = i;
            columns.add(Excel.TableColumn.col(ofSecondOfDay(s).toString(), (UtilizationReportRow r) -> r.values[idx]));
        }
        excel.addSheet(excelUtil.getMessage("reports.usage.sheets.usage"), rows, columns);
        excel.addSheet(excelUtil.getMessage("reports.usage.sheets.legend"),
                excelUtil.getMessage("reports.usage.legend").split("\n"));
        return excel;
    }


    static class UtilizationReportKey extends BasicUtilizationReportKey {
        LocalDate date;
        Facility facility;

        public UtilizationReportKey() {
        }

        public UtilizationReportKey(Utilization u) {
            super(u);
            date = u.timestamp.toLocalDate();
        }

        public UtilizationReportKey prevDay() {
            UtilizationReportKey k = new UtilizationReportKey();
            k.targetId = targetId;
            k.capacityType = capacityType;
            k.usage = usage;
            k.date = date.minusDays(1);
            return k;
        }

        @Override
        public int hashCode() {
            return super.hashCode() ^ date.hashCode();
        }
        @Override
        public boolean equals(Object obj) {
            return super.equals(obj) && date.equals(((UtilizationReportKey) obj).date);
        }
    }

    static class UtilizationReportRow {
        private final int intervalSeconds;
        final UtilizationReportKey key;
        final int[] values;

        UtilizationReportRow(UtilizationReportKey key, int intervalSeconds, int initialValue) {
            this.key = key;
            this.intervalSeconds = intervalSeconds;
            values = new int[SECONDS_IN_DAY / intervalSeconds];
            fill(values, initialValue);
        }

        void setValue(DateTime ts, int value) {
            int idx = ts.getSecondOfDay() / intervalSeconds;
            fill(values, idx, values.length, value);
        }
    }
}
