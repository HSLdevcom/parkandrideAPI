// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import com.mysema.commons.lang.CloseableIterator;
import fi.hsl.parkandride.back.RegionRepository;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.Excel.TableColumn;
import fi.hsl.parkandride.front.ReportParameters;

import java.util.*;

import static com.google.common.collect.Iterators.filter;
import static fi.hsl.parkandride.core.domain.Region.UNKNOWN_REGION;
import static fi.hsl.parkandride.core.service.Excel.TableColumn.col;
import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.sort;
import static org.springframework.util.CollectionUtils.isEmpty;

public class MaxUtilizationReportService extends AbstractReportService {

    public MaxUtilizationReportService(FacilityService facilityService, OperatorService operatorService, ContactService contactService, HubService hubService,
                                       UtilizationRepository utilizationRepository, RegionRepository regionRepository, TranslationService translationService) {
        super(facilityService, operatorService, contactService, hubService, utilizationRepository, translationService, regionRepository);
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
            rows.add(new MaxUtilizationReportRow(hubKey.hub, facilityKeys.get(0), avgPercent, totalCapacity));
        });
        sort(rows);

        Excel excel = new Excel();
        List<TableColumn<MaxUtilizationReportRow>> columns =
                asList(col("Alueen nimi", (MaxUtilizationReportRow r) -> r.hub.name),
                        col("Kunta", (MaxUtilizationReportRow r) -> ctx.regionByHubId.getOrDefault(r.key.targetId, UNKNOWN_REGION).name),
                        col("Operaattori", (MaxUtilizationReportRow r) -> operatorService.getOperator(r.key.facility.operatorId).name),
                        col("Käyttötapa", (MaxUtilizationReportRow r) -> translationService.translate(r.key.usage)),
                        col("Ajoneuvotyyppi", (MaxUtilizationReportRow r) -> translationService.translate(r.key.capacityType)),
                        col("Status", (MaxUtilizationReportRow r) -> translationService.translate(r.key.facility.status)),
                        col("Pysäköintipaikkojen määrä", (MaxUtilizationReportRow r) -> r.totalCapacity),
                        col("Päivätyyppi", (MaxUtilizationReportRow r) -> translationService.translate(r.key.dayType)),
                        col("Keskimääräinen maksimikäyttöaste", (MaxUtilizationReportRow r) -> r.average, excel.percent));
        excel.addSheet("Tiivistelmäraportti", rows, columns);
        excel.addSheet("Selite", "Tiivistelmäraportti kertoo maksimitäyttöasteen valitulla aikavälillä arki-la-su-tarkkuudella.", "",
                "Jos raportiin on valittu alue, johon kuuluu useampi parkkipaikka, niin maksimitäyttöaste on keskiarvo näiden parkkipaikkojen maksimitäyttöasteesta.",
                "Maksimitäyttöaste on päivän ruuhkahuippu kyseisellä parkkipaikalla. Se ilmaisee kuinka täynnä parkkipaikka on ollut, kun se on ollut täysimmillään päivän aikana",
                "Kaikista pysäköintilaitoksista tai -kentistä ei ole saatavilla päivittyvää tietoa, jolloin maksimitäyttöaste on jätetty tyhjäksi tai laskettu vain saatujen tietojen perusteella");

        return excel;
    }

    private Iterator<Utilization> addFilters(Iterator<Utilization> iter, ReportContext ctx, ReportParameters parameters) {
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
        return iter;
    }

    static class MaxUtilizationReportRow implements Comparable<MaxUtilizationReportRow> {
        final Hub hub;
        final MaxUtilizationReportKey key;
        final double average;
        final int totalCapacity;

        MaxUtilizationReportRow(Hub hub, MaxUtilizationReportKey key, double average, int totalCapacity) {
            this.hub = hub;
            this.key = key;
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
