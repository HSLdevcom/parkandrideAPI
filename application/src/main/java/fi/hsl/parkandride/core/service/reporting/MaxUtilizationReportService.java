// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service.reporting;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mysema.commons.lang.CloseableIterator;
import fi.hsl.parkandride.back.RegionRepository;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.*;
import org.joda.time.LocalDate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fi.hsl.parkandride.core.domain.FacilityStatus.INACTIVE;
import static fi.hsl.parkandride.core.domain.FacilityStatus.TEMPORARILY_CLOSED;
import static fi.hsl.parkandride.core.domain.Region.UNKNOWN_REGION;
import static fi.hsl.parkandride.util.MapUtils.entriesToMap;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.sort;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;
import static java.util.stream.StreamSupport.stream;

public class MaxUtilizationReportService extends AbstractReportService {

    private static final String REPORT_NAME = "MaxUtilization";
    private static final Set<FacilityStatus> EXCLUDED_STATES = ImmutableSet.of(INACTIVE, TEMPORARILY_CLOSED);

    public MaxUtilizationReportService(FacilityService facilityService, OperatorService operatorService, ContactService contactService, HubService hubService,
                                       UtilizationRepository utilizationRepository, RegionRepository regionRepository, TranslationService translationService,
                                       FacilityHistoryService facilityHistoryService) {
        super(REPORT_NAME, facilityService, operatorService, contactService, hubService, utilizationRepository, translationService, regionRepository, facilityHistoryService);
    }


    @Override
    protected Excel generateReport(ReportContext ctx, ReportParameters parameters) {
        UtilizationSearch search = toUtilizationSearch(parameters, ctx);
        
        // min available space per [facility, dayType, usage, capacity]
        Map<MaxUtilizationReportKeyWithDate, Integer> facilityStats = getFacilityStats(ctx, parameters, search);

        // Currently returns 0 for dates with excluded status and builtCapacity otherwise
        Map<Long, Map<LocalDate, FacilityStatus>> facilityStatusHistory = getFacilityStatusHistory(parameters, facilityStats);
        Map<MaxUtilizationReportKeyWithDate, Integer> facilityCapacityPerDate = getMaxCapacityPerDate(ctx, facilityStatusHistory, facilityStats);

        // Unavailable capacity history
        Map<MaxUtilizationReportKey, Integer> facilityUnavailableCapacity = getFacilityUnavailableCapacity(parameters, facilityStats);

        // group facility keys by hubs
        Map<HubReportKey, List<MaxUtilizationReportKeyWithDate>> hubStats = groupStatsByHubs(ctx, facilityStats);

        // calculate averages and sums
        List<MaxUtilizationReportRow> rows = createReportRows(ctx, facilityStats, hubStats, facilityCapacityPerDate, facilityStatusHistory, facilityUnavailableCapacity);

        Excel excel = createExcelReport(ctx, rows);

        return excel;
    }

    private ImmutableMap<MaxUtilizationReportKeyWithDate, Integer> getMaxCapacityPerDate(ReportContext ctx, Map<Long, Map<LocalDate, FacilityStatus>> facilityStatusHistory, Map<MaxUtilizationReportKeyWithDate, Integer> facilityStats) {
        return Maps.toMap(facilityStats.keySet(), key -> {
            return Optional.ofNullable(facilityStatusHistory.get(key.targetId)).map(m -> m.get(key.date))
                    .filter(status -> !EXCLUDED_STATES.contains(status))
                    .map(s -> ctx.facilities.get(key.targetId))
                    .map(fac -> fac.builtCapacity.get(key.capacityType))
                    .orElse(0);
        });
    }

    private Map<Long, Map<LocalDate, FacilityStatus>> getFacilityStatusHistory(ReportParameters parameters, Map<MaxUtilizationReportKeyWithDate, Integer> facilityStats) {
        return facilityStats.keySet().stream()
                .map(key -> key.targetId)
                .distinct()
                .collect(toMap(
                        identity(),
                        id -> facilityHistoryService.getStatusHistoryByDay(id, parameters.startDate, parameters.endDate)
                ));
    }

    private Map<MaxUtilizationReportKey, Integer> getFacilityUnavailableCapacity(ReportParameters parameters, Map<MaxUtilizationReportKeyWithDate, Integer> facilityStats) {
        return facilityStats.keySet().stream()
                .map(key -> key.targetId)
                .distinct()
                .flatMap((Long id) -> facilityHistoryService.getUnavailableCapacityHistory(id, parameters.startDate, parameters.endDate)
                        .entrySet().stream()
                        .flatMap(entry -> {
                            final DayType dayType = DayType.valueOf(entry.getKey());
                            return entry.getValue().stream().map(unc -> {
                                final MaxUtilizationReportKey key = new MaxUtilizationReportKey();
                                key.targetId = id;
                                key.dayType = dayType;
                                key.usage = unc.usage;
                                key.capacityType = unc.capacityType;
                                return new AbstractMap.SimpleImmutableEntry<>(key, unc.getCapacity());
                            });
                }))
                .collect(entriesToMap(Math::max));
    }

    private Excel createExcelReport(ReportContext ctx, List<MaxUtilizationReportRow> rows) {
        Excel excel = new Excel();
        Function<MaxUtilizationReportRow, Object> valFn = (MaxUtilizationReportRow r) -> r.average;
        List<Excel.TableColumn<MaxUtilizationReportRow>> columns = asList(
                excelUtil.tcol("reports.utilization.col.hub", (MaxUtilizationReportRow r) -> r.hub.name),
                excelUtil.tcol("reports.utilization.col.region", (MaxUtilizationReportRow r) -> ctx.regionByHubId.getOrDefault(r.key.targetId, UNKNOWN_REGION).name),
                excelUtil.tcol("reports.utilization.col.operator", (MaxUtilizationReportRow r) -> r.operatorNames),
                excelUtil.tcol("reports.utilization.col.usage", (MaxUtilizationReportRow r) -> translationService.translate(r.key.usage)),
                excelUtil.tcol("reports.utilization.col.capacityType", (MaxUtilizationReportRow r) -> translationService.translate(r.key.capacityType)),
                excelUtil.tcol("reports.utilization.col.status", (MaxUtilizationReportRow r) -> translationService.translate(r.key.facility.status)),
                excelUtil.tcol("reports.utilization.col.totalCapacity", (MaxUtilizationReportRow r) -> r.totalCapacity),
                excelUtil.tcol("reports.utilization.col.unavailableCapacity", (MaxUtilizationReportRow r) -> r.unavailableCapacity),
                excelUtil.tcol("reports.utilization.col.dayType", (MaxUtilizationReportRow r) -> translationService.translate(r.key.dayType)),
                excelUtil.tcol("reports.utilization.col.averageMaxUsage", valFn, excel.percent)
        );
        excel.addSheet(excelUtil.getMessage("reports.utilization.sheets.summary"), rows, columns);
        excel.addSheet(excelUtil.getMessage("reports.utilization.sheets.legend"),
                excelUtil.getMessage("reports.utilization.legend").split("\n"));
        return excel;
    }

    private List<MaxUtilizationReportRow> createReportRows(ReportContext ctx, Map<MaxUtilizationReportKeyWithDate, Integer> facilityStats, Map<HubReportKey, List<MaxUtilizationReportKeyWithDate>> hubStats, Map<MaxUtilizationReportKeyWithDate, Integer> facilityCapacityPerDate, Map<Long, Map<LocalDate, FacilityStatus>> statusHistory, Map<MaxUtilizationReportKey, Integer> facilityUnavailableCapacity) {
        List<MaxUtilizationReportRow> rows = new ArrayList<>();

        // At this point, the hub key is grouped by dayType, facility keys still contain the actual dat
        // However, the maximum utilization value has already been calculated for each facility and date.
        hubStats.forEach((hubKey, facilityKeys) -> {
            int totalCapacity = facilityKeys.stream().map(fk -> fk.toReportKey()).distinct()
                    .map(key -> key.facility.builtCapacity.get(hubKey.capacityType)).collect(summingInt(i -> i));

            final Map<LocalDate, Integer> capacityPerDate = facilityKeys.stream().collect(groupingBy(
                    key -> key.date,
                    summingInt(key -> facilityCapacityPerDate.getOrDefault(key, key.facility.builtCapacity.getOrDefault(hubKey.capacityType, 0)))
            ));

            Map<LocalDate, Double> freeSpacesPerDay = facilityKeys.stream()
                    .filter(k -> statusNotExcluded(k, statusHistory))
                    .collect(groupingBy(k -> k.date, summingDouble(k -> facilityStats.getOrDefault(k, 0))));

            Map<LocalDate, Double> utilizationPerDay = freeSpacesPerDay.entrySet().stream()
                    .collect(toMap(
                            entry -> entry.getKey(),
                            entry -> 1.0d - (entry.getValue() / capacityPerDate.get(entry.getKey())) // Fraction per day
                    ));


            final Double averageOfPercentages = utilizationPerDay.values().stream().collect(averagingDouble(i -> i));

            final Map<MaxUtilizationReportKey, Integer> facilityUnavailableCapacity1 = facilityUnavailableCapacity;
            final Integer unavailableCapacity = facilityKeys.stream()
                    .map(k -> k.toReportKey())
                    .collect(summingInt(k -> facilityUnavailableCapacity.getOrDefault(k, 0)));

            rows.add(new MaxUtilizationReportRow(hubKey.hub, facilityKeys.get(0).toReportKey(), operatorNames(ctx, hubKey), averageOfPercentages, totalCapacity, unavailableCapacity));
        });
        sort(rows);
        return rows;
    }

    private String operatorNames(ReportContext ctx, HubReportKey hubKey) {
        return ctx.operatorsByHubId.get(hubKey.targetId).stream().map(op -> op.name.fi).sorted().collect(Collectors.joining(", "));
    }

    private Map<HubReportKey, List<MaxUtilizationReportKeyWithDate>> groupStatsByHubs(ReportContext ctx, Map<MaxUtilizationReportKeyWithDate, Integer> facilityStats) {
        Map<HubReportKey, List<MaxUtilizationReportKeyWithDate>> hubStats = new LinkedHashMap<>();
        facilityStats.forEach((key, val) -> {
            ctx.hubsByFacilityId.getOrDefault(key.targetId, emptyList()).forEach(hub -> {
                List<MaxUtilizationReportKeyWithDate> l = new ArrayList<>();
                l.add(key);

                HubReportKey hubKey = new HubReportKey();
                // almost same key -> just targetId switches from facilityId to
                // hubId
                hubKey.targetId = hub.id;
                hubKey.capacityType = key.capacityType;
                hubKey.usage = key.usage;
                hubKey.dayType = DayType.valueOf(key.date.toDateTimeAtStartOfDay());
                hubKey.hub = hub;
                hubStats.merge(hubKey, l, (o, n) -> {
                    o.addAll(n);
                    return o;
                });
            });
        });
        return hubStats;
    }

    private Map<MaxUtilizationReportKeyWithDate, Integer> getFacilityStats(ReportContext ctx, ReportParameters parameters, UtilizationSearch search) {
        try (CloseableIterator<Utilization> utilizations = utilizationRepository.findUtilizations(search)) {
            return stream(spliteratorUnknownSize(addFilters(utilizations, ctx, parameters), Spliterator.ORDERED), false)
                    .filter(u -> hasBuiltCapacity(u, ctx.facilities))
                    .collect(toMap(
                            u -> new MaxUtilizationReportKeyWithDate(u, ctx.facilities.get(u.facilityId)),
                            u -> u.spacesAvailable,
                            Math::min,
                            LinkedHashMap::new
                    ));
        }
    }

    private boolean hasBuiltCapacity(Utilization u, Map<Long, Facility> facilities) {
        return facilities.get(u.facilityId).builtCapacity.containsKey(u.capacityType);
    }

    private boolean statusNotExcluded(MaxUtilizationReportKeyWithDate key, Map<Long, Map<LocalDate, FacilityStatus>> facilityStatusHistory) {
        return !Optional.ofNullable(facilityStatusHistory.get(key.targetId))
                .map(e -> e.get(key.date))
                .filter(status -> EXCLUDED_STATES.contains(status))
                .isPresent();
    }

    static class MaxUtilizationReportRow implements Comparable<MaxUtilizationReportRow> {
        final Hub hub;
        final MaxUtilizationReportKey key;
        final String operatorNames;
        final double average;
        final int totalCapacity;
        final int unavailableCapacity;

        MaxUtilizationReportRow(Hub hub, MaxUtilizationReportKey key, String operatorNames, double average, int totalCapacity, int unavailableCapacity) {
            this.hub = hub;
            this.key = key;
            this.operatorNames = operatorNames;
            this.average = average;
            this.totalCapacity = totalCapacity;
            this.unavailableCapacity = unavailableCapacity;
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

    static class HubReportKey extends MaxUtilizationReportKey {
        Hub hub;
    }

    static class MaxUtilizationReportKey extends BasicUtilizationReportKey {
        DayType dayType;
        Facility facility;

        public MaxUtilizationReportKey() {
        }

        public MaxUtilizationReportKey(Utilization u) {
            super(u);
            this.dayType = DayType.valueOf(u.timestamp);
        }

        public MaxUtilizationReportKey(Utilization u, Facility facility) {
            this(u);
            this.facility = facility;
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

    static class MaxUtilizationReportKeyWithDate extends BasicUtilizationReportKey {
        LocalDate date;
        Facility facility;

        public MaxUtilizationReportKeyWithDate() {}

        public MaxUtilizationReportKeyWithDate(Utilization u) {
            super(u);
            this.date = u.timestamp.toLocalDate();
        }

        public MaxUtilizationReportKeyWithDate(Utilization u, Facility facility) {
            this(u);
            this.facility = facility;
        }

        public MaxUtilizationReportKey toReportKey() {
            final MaxUtilizationReportKey key = new MaxUtilizationReportKey();
            key.targetId = targetId;
            key.usage = usage;
            key.capacityType = capacityType;
            key.facility = facility;
            key.dayType = DayType.valueOf(date.toDateTimeAtStartOfDay());
            return key;
        }

        @Override
        public int hashCode() {
            return super.hashCode() ^ date.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj) && date.equals(((MaxUtilizationReportKeyWithDate) obj).date);
        }
    }

}
