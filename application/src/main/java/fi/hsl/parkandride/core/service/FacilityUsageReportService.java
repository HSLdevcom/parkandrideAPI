// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import com.mysema.commons.lang.CloseableIterator;
import fi.hsl.parkandride.back.RegionRepository;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.Excel.TableColumn;
import fi.hsl.parkandride.front.ReportParameters;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.*;

import static com.google.common.collect.Iterators.filter;
import static fi.hsl.parkandride.core.domain.DayType.*;
import static fi.hsl.parkandride.core.domain.Permission.REPORT_GENERATE;
import static fi.hsl.parkandride.core.service.AuthenticationService.authorize;
import static fi.hsl.parkandride.core.service.AuthenticationService.getLimitedOperatorId;
import static fi.hsl.parkandride.core.service.Excel.TableColumn.col;
import static fi.hsl.parkandride.core.util.ArgumentValidator.validate;
import static java.time.LocalTime.ofSecondOfDay;
import static java.util.Arrays.asList;
import static java.util.Arrays.fill;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static org.springframework.util.CollectionUtils.isEmpty;

public class FacilityUsageReportService extends AbstractReportService {

    public FacilityUsageReportService(FacilityService facilityService, OperatorService operatorService, ContactService contactService, HubService hubService,
                                      UtilizationRepository utilizationRepository, RegionRepository regionRepository, TranslationService translationService) {
        super(facilityService, operatorService, contactService, hubService, utilizationRepository, translationService, regionRepository);
    }

    @TransactionalRead
    public byte[] reportFacilityUsage(User currentUser, ReportParameters parameters) {
        authorize(currentUser, REPORT_GENERATE);
        int intervalSeconds = validate(parameters.interval).gt(0) * 60;

        ReportContext ctx = new ReportContext(this, getLimitedOperatorId(currentUser));

        UtilizationSearch search = toUtilizationSearch(parameters, ctx);
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
        List<UtilizationReportRow> rows = new ArrayList<>(reportRows.values());
        List<TableColumn<UtilizationReportRow>> columns =
            asList(col("Pysäköintipaikan nimi", (UtilizationReportRow r) -> r.key.facility.name),
                   col("Alue", (UtilizationReportRow r) -> ctx.hubsByFacilityId.getOrDefault(r.key.targetId, emptyList()).stream().map((Hub h) -> h.name.fi).collect(joining(", "))),
                   col("Kunta", (UtilizationReportRow r) -> ctx.regionByFacilityId.get(r.key.targetId).name),
                   col("Operaattori", (UtilizationReportRow r) -> operatorService.getOperator(r.key.facility.operatorId).name),
                   col("Käyttötapa", (UtilizationReportRow r) -> translationService.translate(r.key.usage)),
                   col("Ajoneuvotyyppi", (UtilizationReportRow r) -> translationService.translate(r.key.capacityType)),
                   col("Status", (UtilizationReportRow r) -> translationService.translate(r.key.facility.status)),
                   col("Aukiolo, arki", (UtilizationReportRow r) -> time(r.key.facility.openingHours.byDayType.get(BUSINESS_DAY))),
                   col("Aukiolo, la", (UtilizationReportRow r) -> time(r.key.facility.openingHours.byDayType.get(SATURDAY))),
                   col("Aukiolo, su", (UtilizationReportRow r) -> time(r.key.facility.openingHours.byDayType.get(SUNDAY))),
                   col("Pysäköintipaikkojen määrä", (UtilizationReportRow r) -> r.key.facility.builtCapacity.get(r.key.capacityType)),
                   col("Päivämäärä", (UtilizationReportRow r) -> r.key.date));
        columns = new ArrayList<>(columns);
        for (int s = 0, i = 0; s < SECONDS_IN_DAY; s += intervalSeconds, i++) {
            final int idx = i;
            columns.add(col(ofSecondOfDay(s).toString(), (UtilizationReportRow r) -> r.values[idx]));
        }
        excel.addSheet("Käyttöasteraportti", rows, columns);
        excel.addSheet("Selite",
                       "Tämä dokumentti kertoo yksittäisten liityntäpysäköintilaitosten vapaiden paikkojen määrän eri ajanhetkinä eroteltuna ajoneuvotyypeittäin",
                       "Kaikista pysäköintilaitoksista tai -kentistä ei ole saatavilla päivittyvää tietoa");
        return excel.toBytes();
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
