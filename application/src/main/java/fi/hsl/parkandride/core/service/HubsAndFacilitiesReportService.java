// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.back.RegionRepository;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.front.ReportParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fi.hsl.parkandride.core.domain.CapacityType.*;
import static fi.hsl.parkandride.core.domain.DayType.*;
import static fi.hsl.parkandride.core.domain.Permission.REPORT_GENERATE;
import static fi.hsl.parkandride.core.service.AuthenticationService.authorize;
import static fi.hsl.parkandride.core.service.AuthenticationService.getLimitedOperatorId;
import static fi.hsl.parkandride.core.service.Excel.TableColumn.col;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class HubsAndFacilitiesReportService extends AbstractReportService {

    public HubsAndFacilitiesReportService(FacilityService facilityService, OperatorService operatorService, ContactService contactService, HubService hubService,
                                          UtilizationRepository utilizationRepository, RegionRepository regionRepository, TranslationService translationService) {
        super(facilityService, operatorService, contactService, hubService, utilizationRepository, translationService, regionRepository);
    }

    protected static CharSequence addressText(Address address) {
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
                              col("Kaikki moottoriajoneuvot", (Hub h) -> capacitySum(ctx, h.id, motorCapacities)),
                              col("Kaikki polkupyörät", (Hub h) -> capacitySum(ctx, h.id, bicycleCapacities)),
                              col("Henkilöauto", (Hub h) -> capacitySum(ctx, h.id, CAR)),
                              col("Invapaikka", (Hub h) -> capacitySum(ctx, h.id, DISABLED)),
                              col("Sähköauto", (Hub h) -> capacitySum(ctx, h.id, ELECTRIC_CAR)),
                              col("Moottoripyörä", (Hub h) -> capacitySum(ctx, h.id, MOTORCYCLE)),
                              col("Polkupyörä", (Hub h) -> capacitySum(ctx, h.id, BICYCLE)),
                              col("Polkupyörä, lukittu tila", (Hub h) -> capacitySum(ctx, h.id, BICYCLE_SECURE_SPACE)),
                              col("Pysäköintipaikat", (Hub h) -> ctx.facilitiesByHubId.getOrDefault(h.id, emptyList()).stream().map((Facility f) -> f.name.fi).collect(toList()))));
    }

    private int capacitySum(ReportContext ctx, long hubId, CapacityType... types) {
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

}
