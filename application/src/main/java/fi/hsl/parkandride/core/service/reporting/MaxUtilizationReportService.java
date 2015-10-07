// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service.reporting;

import com.mysema.commons.lang.CloseableIterator;
import fi.hsl.parkandride.back.RegionRepository;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.*;
import fi.hsl.parkandride.front.ReportParameters;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static fi.hsl.parkandride.core.domain.Region.UNKNOWN_REGION;
import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.sort;
import static java.util.stream.Collectors.joining;

public class MaxUtilizationReportService extends AbstractReportService {

    private static final String REPORT_NAME = "MaxUtilization";

    public MaxUtilizationReportService(FacilityService facilityService, OperatorService operatorService, ContactService contactService, HubService hubService,
                                       UtilizationRepository utilizationRepository, RegionRepository regionRepository, TranslationService translationService) {
        super(REPORT_NAME, facilityService, operatorService, contactService, hubService, utilizationRepository, translationService, regionRepository);
    }


    @Override
    protected Excel generateReport(ReportContext ctx, ReportParameters parameters) {
        UtilizationSearch search = toUtilizationSearch(parameters, ctx);

        // min available space per [facility, dayType, usage, capacity]
        Map<MaxUtilizationReportKey, Integer> facilityStats = new LinkedHashMap<>();
        try (CloseableIterator<Utilization> utilizations = utilizationRepository.findUtilizations(search)) {
            addFilters(utilizations, ctx, parameters).forEachRemaining(u -> {
                MaxUtilizationReportKey key = new MaxUtilizationReportKey(u);
                key.facility = ctx.facilities.get(u.facilityId);
                facilityStats.merge(key, u.spacesAvailable, (o, n) -> min(o, n));
            });
        }

        // group facility keys by hubs
        Map<MaxUtilizationReportKey, List<MaxUtilizationReportKey>> hubStats = new LinkedHashMap<>();
        facilityStats.forEach((key, val) -> {
            ctx.hubsByFacilityId.getOrDefault(key.targetId, emptyList()).forEach(hub -> {
                ArrayList<MaxUtilizationReportKey> l = new ArrayList<>();
                l.add(key);

                MaxUtilizationReportKey hubKey = new MaxUtilizationReportKey();
                // almost same key -> just targetId switches from facilityId to
                // hubId
                hubKey.targetId = hub.id;
                hubKey.capacityType = key.capacityType;
                hubKey.usage = key.usage;
                hubKey.dayType = key.dayType;
                hubKey.hub = hub;
                hubStats.merge(hubKey, l, (o, n) -> {
                    o.addAll(n);
                    return o;
                });
            });
        });

        // calculate averages and sums
        List<MaxUtilizationReportRow> rows = new ArrayList<>();
        hubStats.forEach((hubKey, facilityKeys) -> {
            double avgPercent = facilityKeys.stream()
                    .mapToDouble(key -> 1.0 - facilityStats.get(key) / (double) key.facility.builtCapacity.get(key.capacityType))
                    .average().orElse(0);
            int totalCapacity = facilityKeys.stream().mapToInt(key -> key.facility.builtCapacity.get(key.capacityType)).sum();
            final String operatorNames = facilityKeys.stream()
                    .map(f -> operatorService.getOperator(f.targetId))
                    .map(o -> o.name.fi)
                    .sorted()
                    .collect(joining(", "));
            rows.add(new MaxUtilizationReportRow(hubKey.hub, facilityKeys.get(0), operatorNames, avgPercent, totalCapacity));
        });
        sort(rows);

        Excel excel = new Excel();
        Function<MaxUtilizationReportRow,Object> valFn = (MaxUtilizationReportRow r) -> r.average;
        List<Excel.TableColumn<MaxUtilizationReportRow>> columns = asList(
                excelUtil.tcol("reports.utilization.col.hub", (MaxUtilizationReportRow r) -> r.hub.name),
                excelUtil.tcol("reports.utilization.col.region", (MaxUtilizationReportRow r) -> ctx.regionByHubId.getOrDefault(r.key.targetId, UNKNOWN_REGION).name),
                excelUtil.tcol("reports.utilization.col.operator", (MaxUtilizationReportRow r) -> r.operatorNames),
                excelUtil.tcol("reports.utilization.col.usage", (MaxUtilizationReportRow r) -> translationService.translate(r.key.usage)),
                excelUtil.tcol("reports.utilization.col.capacityType", (MaxUtilizationReportRow r) -> translationService.translate(r.key.capacityType)),
                excelUtil.tcol("reports.utilization.col.status", (MaxUtilizationReportRow r) -> translationService.translate(r.key.facility.status)),
                excelUtil.tcol("reports.utilization.col.totalCapacity", (MaxUtilizationReportRow r) -> r.totalCapacity),
                excelUtil.tcol("reports.utilization.col.dayType", (MaxUtilizationReportRow r) -> translationService.translate(r.key.dayType)),
                excelUtil.tcol("reports.utilization.col.averageMaxUsage", valFn, excel.percent)
        );
        excel.addSheet(excelUtil.getMessage("reports.utilization.sheets.summary"), rows, columns);
        excel.addSheet(excelUtil.getMessage("reports.utilization.sheets.legend"),
                excelUtil.getMessage("reports.utilization.legend").split("\n"));

        return excel;
    }


    static class MaxUtilizationReportRow implements Comparable<MaxUtilizationReportRow> {
        final Hub hub;
        final MaxUtilizationReportKey key;
        final String operatorNames;
        final double average;
        final int totalCapacity;

        MaxUtilizationReportRow(Hub hub, MaxUtilizationReportKey key, String operatorNames, double average, int totalCapacity) {
            this.hub = hub;
            this.key = key;
            this.operatorNames = operatorNames;
            this.average = average;
            this.totalCapacity = totalCapacity;
        }

        @Override
        public int compareTo(MaxUtilizationReportRow o) {
            int c = hub.name.fi.compareTo(o.hub.name.fi);
            if (c != 0) {
                return c;
            }
            c = key.usage.compareTo(o.key.usage);
            if (c != 0) {
                return c;
            }
            c = key.capacityType.compareTo(o.key.capacityType);
            if (c != 0) {
                return c;
            }
            return key.dayType.compareTo(o.key.dayType);
        }
    }

    static class MaxUtilizationReportKey extends BasicUtilizationReportKey {
        DayType dayType;
        Facility facility;
        Hub hub;

        public MaxUtilizationReportKey() {
        }

        public MaxUtilizationReportKey(Utilization u) {
            super(u);
            this.dayType = DayType.valueOf(u.timestamp);
        }

        @Override
        public int hashCode() {
            return super.hashCode() ^ dayType.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj) && dayType.equals(((MaxUtilizationReportKey) obj).dayType);
        }
    }

}
