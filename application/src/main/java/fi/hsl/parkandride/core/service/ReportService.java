// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import static com.google.common.collect.Iterators.filter;
import static org.joda.time.format.DateTimeFormat.forPattern;
import static org.springframework.util.CollectionUtils.isEmpty;
import com.mysema.commons.lang.CloseableIterator;
import fi.hsl.parkandride.back.RegionRepository;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.Excel.TableColumn;
import fi.hsl.parkandride.front.ReportParameters;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import java.util.*;
import static fi.hsl.parkandride.core.domain.CapacityType.*;
import static fi.hsl.parkandride.core.domain.DayType.*;
import static fi.hsl.parkandride.core.domain.Permission.REPORT_GENERATE;
import static fi.hsl.parkandride.core.domain.Region.UNKNOWN_REGION;
import static fi.hsl.parkandride.core.service.AuthenticationService.authorize;
import static fi.hsl.parkandride.core.service.AuthenticationService.getLimitedOperatorId;
import static fi.hsl.parkandride.core.service.Excel.TableColumn.col;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.time.LocalTime.ofSecondOfDay;
import static java.util.Arrays.asList;
import static java.util.Arrays.fill;
import static java.util.Collections.emptyList;
import static java.util.Collections.sort;
import static java.util.stream.Collectors.*;

public class ReportService {
    private static final int SECONDS_IN_DAY = 60 * 60 * 24;

    final FacilityService facilityService;
    final OperatorService operatorService;
    private final ContactService contactService;
    final HubService hubService;
    private final UtilizationRepository utilizationRepository;
    private final TranslationService translationService;
    final RegionRepository regionRepository;

    public ReportService(FacilityService facilityService, OperatorService operatorService, ContactService contactService, HubService hubService,
                         UtilizationRepository utilizationRepository, RegionRepository regionRepository, TranslationService translationService) {
        this.facilityService = facilityService;
        this.operatorService = operatorService;
        this.contactService = contactService;
        this.hubService = hubService;
        this.utilizationRepository = utilizationRepository;
        this.regionRepository = regionRepository;
        this.translationService = translationService;
    }

    public byte[] reportHubsAndFacilities(User currentUser, ReportParameters parameters) {
        authorize(currentUser, REPORT_GENERATE);

        ReportContext ctx = new ReportContext(this, getLimitedOperatorId(currentUser));

        Excel excel = new Excel();
        addRegionsSheet(excel, new ArrayList<>(ctx.hubs.values()), ctx);
        addFacilitiesSheet(excel, new ArrayList<>(ctx.facilities.values()), ctx);
        excel.addSheet("Selite", "Tämä dokumentti on vedos https://p.hsl.fi/ -sivuston tiedoista", "",
                       "Alueet-välilehdelle on koostettu kaikki järjestelmään syötetyt alueet, niihin liitetyt tiedot ja pysäköintipaikat",
                       "Pysäköintipaikat-välilehdelle on koostettu riveittäin kaikki järjestelmään syötetyt pysäköintipaikat tietoineen",
                       "", "Pysäköintipaikka vastaa yksittäistä pysäköintikenttää tai -laitosta",
                       "Alue vastaa joukkoliikenteen solmukohtaa, kuten juna- tai linja-autoasemaa",
                       "Pysäköintipaikat on järjestetty alueittain",
                       "Yksi pysäköintipaikka voi kuulua useampaan alueeseen",
                       "Kaikki koordinaatit noudattavat EPSG:4326 -järjestelmää");
        return excel.toBytes();
    }

    private void addFacilitiesSheet(Excel excel, List<Facility> facilities, ReportContext ctx) {
        excel.addSheet("Pysäköintipaikat", facilities,
                       asList(col("Pysäköintipaikan nimi", (Facility f) -> f.name),
                              col("Aliakset", (Facility f) -> join(", ", f.aliases)),
                              col("Kuuluu alueisiin", (Facility f) -> ctx.hubsByFacilityId.getOrDefault(f.id, emptyList()).stream().map((Hub h) -> h.name.fi).collect(joining(", "))),
                              col("Operaattori", (Facility f) -> ctx.operators.get(f.operatorId).name),
                              col("Status", (Facility f) -> translationService.translate(f.status)),
                              col("Statuksen lisätiedot / poikkeustiedote", (Facility f) -> f.statusDescription),
                              col("Sijainti, pituuspiiri", (Facility f) -> f.location.getCentroid().getX()),
                              col("Sijainti, leveyspiiri", (Facility f) -> f.location.getCentroid().getY()),
                              col("Aukiolo, arki", (Facility f) -> time(f.openingHours.byDayType.get(BUSINESS_DAY))),
                              col("Aukiolo, la", (Facility f) -> time(f.openingHours.byDayType.get(SATURDAY))),
                              col("Aukiolo, su", (Facility f) -> time(f.openingHours.byDayType.get(SUNDAY))),
                              col("Aukiolo, lisätiedot", (Facility f) -> f.openingHours.info),
                              col("Kaikki moottoriajoneuvot", (Facility f) -> capacitySum(f.builtCapacity, motorCapacityList)),
                              col("Kaikki polkupyörät", (Facility f) -> capacitySum(f.builtCapacity, bicycleCapacityList)),
                              col("Henkilöauto", (Facility f) -> f.builtCapacity.get(CAR)),
                              col("Invapaikka", (Facility f) -> f.builtCapacity.get(DISABLED)),
                              col("Sähköauto", (Facility f) -> f.builtCapacity.get(ELECTRIC_CAR)),
                              col("Moottoripyörä", (Facility f) -> f.builtCapacity.get(MOTORCYCLE)),
                              col("Polkupyörä", (Facility f) -> f.builtCapacity.get(BICYCLE)),
                              col("Polkupyörä, lukittu tila", (Facility f) -> f.builtCapacity.get(BICYCLE_SECURE_SPACE)),
                              col("Hinnoittelu", (Facility f) -> f.pricing.stream().map((p) -> translationService.translate(p.usage)).distinct().collect(joining(", "))),
                              col("Maksutavat", (Facility f) -> f.paymentInfo.paymentMethods.stream().map(m -> translationService.translate(m)).collect(joining(", "))),
                              col("Maksutapojen lisätiedot", (Facility f) -> f.paymentInfo.detail),
                              col("Palvelut", (Facility f) -> f.services.stream().map(s -> translationService.translate(s)).collect(joining(", "))),
                              col("Yhteystiedot, Hätänumero", (Facility f) -> contactText(f.contacts.emergency)),
                              col("Yhteystiedot, Operaattorin yhteystieto", (Facility f) -> contactText(f.contacts.operator)),
                              col("Yhteystiedot, Palveluyhtiö", (Facility f) -> contactText(f.contacts.service))));

    }

    private void addRegionsSheet(Excel excel, List<Hub> hubs, ReportContext ctx) {
        excel.addSheet("Alueet", hubs,
                       asList(col("Alueen nimi", (Hub h) -> h.name),
                              col("Käyntiosoite", (Hub h) -> addressText(h.address)),
                              col("Sijainti, pituuspiiri", (Hub h) -> h.location.getX()),
                              col("Sijainti, leveyspiiri", (Hub h) -> h.location.getY()),
                              col("Kaikki moottoriajoneuvot", (Hub h) -> capcitySum(ctx, h.id, motorCapacities)),
                              col("Kaikki polkupyörät", (Hub h) -> capcitySum(ctx, h.id, bicycleCapacities)),
                              col("Henkilöauto", (Hub h) -> capcitySum(ctx, h.id, CAR)),
                              col("Invapaikka", (Hub h) -> capcitySum(ctx, h.id, DISABLED)),
                              col("Sähköauto", (Hub h) -> capcitySum(ctx, h.id, ELECTRIC_CAR)),
                              col("Moottoripyörä", (Hub h) -> capcitySum(ctx, h.id, MOTORCYCLE)),
                              col("Polkupyörä", (Hub h) -> capcitySum(ctx, h.id, BICYCLE)),
                              col("Polkupyörä, lukittu tila", (Hub h) -> capcitySum(ctx, h.id, BICYCLE_SECURE_SPACE)),
                              col("Pysäköintipaikat", (Hub h) -> ctx.facilitiesByHubId.getOrDefault(h.id, emptyList()).stream().map((Facility f) -> f.name.fi).collect(toList()))));
    }

    @TransactionalRead
    public byte[] reportFacilityUsage(User currentUser, ReportParameters parameters) {
        authorize(currentUser, REPORT_GENERATE);
        int intervalSeconds = parameters.interval * 60;

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

    UtilizationSearch toUtilizationSearch(ReportParameters parameters, final ReportContext ctx) {
        UtilizationSearch search = new UtilizationSearch();
        DateTimeFormatter finnishDateFormat = forPattern("d.M.yyyy");
        search.start = finnishDateFormat.parseLocalDate(parameters.startDate).toDateTimeAtStartOfDay();
        if (parameters.endDate == null) {
            search.end = new LocalDate().plusDays(1).toDateTimeAtStartOfDay();
        } else {
            search.end = finnishDateFormat.parseLocalDate(parameters.endDate).toDateTimeAtStartOfDay();
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

    @TransactionalRead
    public byte[] reportMaxUtilization(User currentUser, ReportParameters parameters) {
        authorize(currentUser, REPORT_GENERATE);

        ReportContext ctx = new ReportContext(this, getLimitedOperatorId(currentUser));

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

        return excel.toBytes();
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

    private int capcitySum(ReportContext ctx, long hubId, CapacityType... types) {
        List<Facility> facilities = ctx.facilitiesByHubId.getOrDefault(hubId, emptyList());
        int sum = 0;
        for (CapacityType type : types) {
            sum += facilities.stream().mapToInt((Facility f) -> f.builtCapacity.getOrDefault(type, 0)).sum();
        }
        return sum;
    }

    private static int capacitySum(Map<CapacityType, Integer> capacityValues, List<CapacityType> capacityTypes) {
        return capacityTypes.stream().mapToInt(type -> capacityValues.getOrDefault(type, 0)).sum();
    }

    private static String time(TimeDuration time) {
        return time == null ? null : format("%02d:%02d - %02d:%02d", time.from.getHour(), time.from.getMinute(), time.until.getHour(), time.until.getMinute());
    }

    private CharSequence contactText(Long contactId) {
        if (contactId == null) {
            return null;
        }
        Contact contact = contactService.getContact(contactId);

        StringBuilder sb = new StringBuilder();
        sb.append(contact.name.fi);
        if (contact.phone != null) {
            sb.append(", ").append(contact.phone);
        }
        if (contact.email != null) {
            sb.append(", ").append(contact.email);
        }
        if (contact.address != null) {
            sb.append(", ").append(addressText(contact.address));
        }
        if (contact.openingHours != null) {
            sb.append(", ").append(contact.openingHours.fi);
        }
        if (contact.info != null) {
            sb.append(", ").append(contact.info.fi);
        }
        return sb;
    }

    private static CharSequence addressText(Address address) {
        if (address == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        MultilingualString street = address.getStreetAddress();
        if (street != null) {
            sb.append(street.fi);
        }
        if (address.postalCode != null) {
            sb.append(", ").append(address.postalCode);
        }
        if (address.city != null) {
            sb.append(", ").append(address.city.fi);
        }
        return sb;
    }
}
