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
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.collect.Lists.newArrayList;
import static fi.hsl.parkandride.core.domain.FacilityStatus.INACTIVE;
import static fi.hsl.parkandride.core.domain.FacilityStatus.TEMPORARILY_CLOSED;
import static fi.hsl.parkandride.core.domain.Region.UNKNOWN_REGION;
import static fi.hsl.parkandride.util.MapUtils.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.sort;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.*;
import static java.util.stream.StreamSupport.stream;

public class MaxUtilizationReportService extends AbstractReportService {

    private static final String REPORT_NAME = "MaxUtilization";
    private static final Set<FacilityStatus> EXCLUDED_STATES = ImmutableSet.of(INACTIVE, TEMPORARILY_CLOSED);
    private static final Collector<Map.Entry<MaxUtilizationReportKeyWithDate, FacilityRowInfo>, ?, MaxUtilizationReportInfo> toMaxUtilizationReportInfo = Collector.of(
            MaxUtilizationReportInfo::new,
            (info, entry) -> info.addRow(entry.getKey(), entry.getValue()),
            (info1, info2) -> {
                info2.rows.forEach(info1::addRow);
                return info1;
            }
    );

    public MaxUtilizationReportService(FacilityService facilityService, OperatorService operatorService, ContactService contactService, HubService hubService,
                                       UtilizationRepository utilizationRepository, RegionRepository regionRepository, TranslationService translationService,
                                       FacilityHistoryService facilityHistoryService) {
        super(REPORT_NAME, facilityService, operatorService, contactService, hubService, utilizationRepository, translationService, regionRepository, facilityHistoryService);
    }

    private static boolean hasBuiltCapacity(Utilization u, Map<Long, Facility> facilities) {
        return facilities.get(u.facilityId).builtCapacity.containsKey(u.capacityType);
    }

    @Override
    protected Excel generateReport(ReportContext ctx, ReportParameters parameters) {
        // Filtered utilizations to spaces available
        // (Facility, LocalDate, CapacityType, Usage) -> [totalCapacity,unavailableCapacity,availableSpaces]
        MaxUtilizationReportInfo reportInfo = getReportInfo(ctx, parameters);

        // group facility keys by hubs
        Map<HubReportKey, List<MaxUtilizationReportKeyWithDate>> hubStats = groupKeysByHubs(ctx, reportInfo.rows.keySet());

        // calculate averages and sums
        List<MaxUtilizationReportRow> rows = createReportRows(ctx, hubStats, reportInfo);

        Excel excel = createExcelReport(ctx, rows);

        return excel;
    }

    private MaxUtilizationReportInfo getReportInfo(ReportContext ctx, ReportParameters params) {
        Map<MaxUtilizationReportKeyWithDate, Integer> facilityStats = getFacilityStats(ctx, params, toUtilizationSearch(params, ctx));
        Set<Long> facilityIds = facilityStats.keySet().stream().map(key -> key.targetId).distinct().collect(toSet());
        // Historical information
        Map<Long, Map<LocalDate, FacilityStatus>> facilityStatusHistory = getFacilityStatusHistory(facilityIds, params.startDate, params.endDate);
        Map<Long, Map<LocalDate, FacilityCapacity>> facilityCapacityHistory = getFacilityCapacityHistory(facilityIds, params.startDate, params.endDate);

        Map<MaxUtilizationReportKeyWithDate, Integer> facilityCapacityPerDate = getMaxCapacityPerDate(ctx, facilityStatusHistory, facilityStats.keySet());
        Map<MaxUtilizationReportKey, Integer> facilityUnavailableCapacity = getFacilityUnavailableCapacity(ctx, facilityCapacityHistory);

        return facilityStats.entrySet().stream().map(mappingValue((key, spacesAvailable) -> {
            final Facility facility = ctx.facilities.get(key.targetId);

            final FacilityRowInfo info = new FacilityRowInfo();
            info.facility = key.facility;
            info.spacesAvailable = spacesAvailable;
            info.totalCapacity = Optional.ofNullable(facilityCapacityPerDate.get(key)).orElse(facility.builtCapacity.get(key.capacityType));
            info.unavailableCapacity = facilityUnavailableCapacity.get(key.toReportKey());
            info.status = facilityStatusHistory.get(key.targetId).get(key.date);
            info.date = key.date;
            return info;
        })).collect(toMaxUtilizationReportInfo);
    }

    private ImmutableMap<MaxUtilizationReportKeyWithDate, Integer> getMaxCapacityPerDate(ReportContext ctx, Map<Long, Map<LocalDate, FacilityStatus>> facilityStatusHistory, Set<MaxUtilizationReportKeyWithDate> reportKeys) {
        return Maps.toMap(reportKeys, key -> {
            return Optional.ofNullable(facilityStatusHistory.get(key.targetId)).map(m -> m.get(key.date))
                    .filter(status -> !EXCLUDED_STATES.contains(status))
                    .map(s -> ctx.facilities.get(key.targetId))
                    .map(fac -> fac.builtCapacity.get(key.capacityType))
                    .orElse(0);
        });
    }

    private Map<Long, Map<LocalDate, FacilityStatus>> getFacilityStatusHistory(Set<Long> facilityIds, LocalDate startDate, LocalDate endDate) {
        return Maps.toMap(facilityIds, id -> facilityHistoryService.getStatusHistoryByDay(id, startDate, endDate));
    }

    private Map<Long, Map<LocalDate, FacilityCapacity>> getFacilityCapacityHistory(Set<Long> facilityIds, LocalDate startDate, LocalDate endDate) {
        return Maps.toMap(facilityIds, id -> facilityHistoryService.getCapacityHistory(id, startDate, endDate));
    }

    private Map<MaxUtilizationReportKey, Integer> getFacilityUnavailableCapacity(ReportContext ctx, Map<Long, Map<LocalDate, FacilityCapacity>> capacityHistory) {
        Set<MaxUtilizationReportKeyWithDate> keysForUnavailableDates = capacityHistory.entrySet().stream()
                .flatMap(mappingEntry((id, val) -> val.entrySet().stream()
                        .flatMap(mappingEntry((LocalDate date, FacilityCapacity capacity) -> capacity.unavailableCapacities.stream()
                                .map((UnavailableCapacity uc) -> {
                                    final MaxUtilizationReportKeyWithDate key = new MaxUtilizationReportKeyWithDate();
                                    key.targetId = id;
                                    key.facility = ctx.facilities.get(id);
                                    key.date = date;
                                    key.capacityType = uc.capacityType;
                                    key.usage = uc.usage;
                                    return key;
                                })))))
                .collect(toSet());

        return Maps.toMap(keysForUnavailableDates, key -> {
            final FacilityCapacity currentCapacities = new FacilityCapacity(ctx.facilities.get(key.targetId));
            return capacityHistory
                    .getOrDefault(key.targetId, emptyMap())
                    .getOrDefault(key.date, currentCapacities)
                    .unavailableCapacities
                    .stream()
                    .filter(uc -> uc.capacityType == key.capacityType)
                    .filter(uc -> uc.usage == key.usage)
                    .map(uc -> uc.capacity)
                    .findFirst().orElse(0);
        }).entrySet().stream().map(mappingKey((key, val) -> key.toReportKey())).collect(entriesToMap(Math::max));
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

    private List<MaxUtilizationReportRow> createReportRows(ReportContext ctx, Map<HubReportKey, List<MaxUtilizationReportKeyWithDate>> hubStats, MaxUtilizationReportInfo reportInfo) {
        List<MaxUtilizationReportRow> rows = new ArrayList<>();

        // At this point, the hub key is grouped by dayType, facility keys still contain the actual dat
        // However, the maximum utilization value has already been calculated for each facility and date.
        hubStats.forEach((hubKey, facilityKeys) -> {
            final Integer totalCapacity = facilityKeys.stream()
                    .map(k -> k.targetId)
                    .distinct()
                    .map(id -> ctx.facilities.get(id))
                    .collect(summingInt(f -> f.builtCapacity.get(hubKey.capacityType)));

            final List<FacilityRowInfo> facilityInfos = facilityKeys.stream().map(k -> reportInfo.rows.get(k)).collect(toList());
            final int unavailableCapacity = facilityInfos.stream().collect(summingInt(row -> firstNonNull(row.unavailableCapacity, 0)));

            final Map<LocalDate, Integer> capacityPerDate = facilityInfos.stream()
                    .collect(groupingBy(info -> info.date, summingInt(info -> info.totalCapacity)));
            final Map<LocalDate, Double> freeSpacesPerDate = facilityInfos.stream()
                    .filter(info -> !EXCLUDED_STATES.contains(info.status))
                    .collect(groupingBy(info -> info.date, summingDouble(info -> info.spacesAvailable)));

            final Double averageOfPercentages = freeSpacesPerDate.entrySet().stream()
                    .map(mappingValue((date, freeSpaces) -> {
                        final Integer capacity = capacityPerDate.get(date);
                        return capacity == 0 ? 0.0d : 1.0d - (freeSpaces / ((double) capacity));
                    }))
                    .collect(averagingDouble(e -> e.getValue()));

            rows.add(new MaxUtilizationReportRow(hubKey.hub, facilityKeys.get(0).toReportKey(), operatorNames(ctx, hubKey), averageOfPercentages, totalCapacity, unavailableCapacity));
        });
        sort(rows);
        return rows;
    }

    private String operatorNames(ReportContext ctx, HubReportKey hubKey) {
        return ctx.operatorsByHubId.get(hubKey.targetId).stream().map(op -> op.name.fi).sorted().collect(Collectors.joining(", "));
    }

    private Map<HubReportKey, List<MaxUtilizationReportKeyWithDate>> groupKeysByHubs(ReportContext ctx, Set<MaxUtilizationReportKeyWithDate> reportKeys) {
        Map<HubReportKey, List<MaxUtilizationReportKeyWithDate>> hubStats = new LinkedHashMap<>();
        reportKeys.forEach(key -> {
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

    private static class MaxUtilizationReportInfo {

        Map<MaxUtilizationReportKeyWithDate, FacilityRowInfo> rows = new LinkedHashMap<>();
        Map<MaxUtilizationReportKey, List<FacilityRowInfo>> groupedByDayType = new LinkedHashMap<>();

        public MaxUtilizationReportInfo addRow(MaxUtilizationReportKeyWithDate key, FacilityRowInfo info) {
            rows.merge(key, info, (r1, r2) -> {
                throw new IllegalArgumentException(String.format("Duplicate keys encountered: <%s> <%s>", r1, r2));
            });
            groupedByDayType.computeIfAbsent(key.toReportKey(), k -> newArrayList()).add(info);
            return this;
        }

    }

    private static class FacilityRowInfo {
        Facility facility;
        Integer spacesAvailable;
        Integer totalCapacity;
        Integer unavailableCapacity;
        FacilityStatus status;
        LocalDate date;
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

        public MaxUtilizationReportKeyWithDate() {
        }

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
