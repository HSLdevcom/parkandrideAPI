// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.domain.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.hsl.parkandride.core.domain.CapacityType.*;
import static fi.hsl.parkandride.core.domain.DayType.*;
import static fi.hsl.parkandride.core.domain.Permission.REPORT_GENERATE;
import static fi.hsl.parkandride.core.service.AuthenticationService.authorize;
import static fi.hsl.parkandride.core.service.Excel.TableColumn.col;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

public class ReportService {
    private final FacilityService facilityService;
    private final OperatorService operatorService;
    private final ContactService contactService;
    private final HubService hubService;
    private final TranslationService translationService;

    public ReportService(FacilityService facilityService, OperatorService operatorService,
                         ContactService contactService, HubService hubService, TranslationService translationService) {
        this.facilityService = facilityService;
        this.operatorService = operatorService;
        this.contactService = contactService;
        this.hubService = hubService;
        this.translationService = translationService;
    }

    public byte[] reportHubsAndFacilities(User currentUser) {
        authorize(currentUser, REPORT_GENERATE);

        // fetch data and precalculate some values
        List<Facility> facilities = getFacilities();
        List<Hub> hubs = getHubs();
        Map<Long, Facility> facilitiesByFacilityId = facilities.stream().collect(toMap((Facility f) -> f.id, identity()));
        Map<Long, List<Facility>> facilitiesByHubId = new HashMap<>();
        Map<Long, List<Hub>> hubsByFacilityId = new HashMap<>();

        hubs.stream().forEach(hub -> {
            facilitiesByHubId.put(hub.id, hub.facilityIds.stream().map(id -> facilitiesByFacilityId.get(id)).collect(toList()));
            for (long facilityId : hub.facilityIds) {
                List<Hub> hubList = hubsByFacilityId.get(facilityId);
                if (hubList == null) {
                    hubList = new ArrayList<>();
                    hubsByFacilityId.put(facilityId, hubList);
                }
                hubList.add(hub);
            }
        });

        Excel excel = new Excel();
        addRegionsSheet(excel, hubs, facilitiesByHubId);
        addFacilitiesSheet(excel, facilities, hubsByFacilityId);
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

    private void addFacilitiesSheet(Excel excel, List<Facility> facilities, Map<Long, List<Hub>> hubsByFacilityId) {
        excel.addSheet("Pysäköintipaikat", facilities,
                       asList(col("Pysäköintipaikan nimi", (Facility f) -> f.name),
                              col("Aliakset", (Facility f) -> join(", ", f.aliases)),
                              col("Kuuluu alueisiin", (Facility f) -> hubsByFacilityId.get(f.id).stream().map(h -> h.name.fi).collect(joining(", "))),
                              col("Operaattori", (Facility f) -> operatorService.getOperator(f.operatorId).name),
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
                              col("Hinnoittelu",
                                  (Facility f) -> f.pricing.stream().map((p) -> translationService.translate(p.usage)).distinct()
                                                  .collect(joining(", "))),
                              col("Maksutavat", (Facility f) -> f.paymentInfo.paymentMethods.stream().map(m -> translationService.translate(m)).collect(joining(", "))),
                              col("Maksutapojen lisätiedot", (Facility f) -> f.paymentInfo.detail),
                              col("Palvelut", (Facility f) -> f.services.stream().map(s -> translationService.translate(s)).collect(joining(", "))),
                              col("Yhteystiedot, Hätänumero", (Facility f) -> contactText(f.contacts.emergency)),
                              col("Yhteystiedot, Operaattorin yhteystieto", (Facility f) -> contactText(f.contacts.operator)),
                              col("Yhteystiedot, Palveluyhtiö", (Facility f) -> contactText(f.contacts.service))));

    }

    private void addRegionsSheet(Excel excel, List<Hub> hubs, Map<Long, List<Facility>> facilitiesByHubId) {
        excel.addSheet("Alueet", hubs,
                       asList(col("Alueen nimi", h -> h.name),
                              col("Käyntiosoite", h -> addressText(h.address)),
                              col("Sijainti, pituuspiiri", h -> h.location.getX()),
                              col("Sijainti, leveyspiiri", h -> h.location.getY()),
                              col("Kaikki moottoriajoneuvot", h -> capcitySum(facilitiesByHubId, h.id, motorCapacities)),
                              col("Kaikki polkupyörät", h -> capcitySum(facilitiesByHubId, h.id, bicycleCapacities)),
                              col("Henkilöauto", h -> capcitySum(facilitiesByHubId, h.id, CAR)),
                              col("Invapaikka", h -> capcitySum(facilitiesByHubId, h.id, DISABLED)),
                              col("Sähköauto", h -> capcitySum(facilitiesByHubId, h.id, ELECTRIC_CAR)),
                              col("Moottoripyörä", h -> capcitySum(facilitiesByHubId, h.id, MOTORCYCLE)),
                              col("Polkupyörä", h -> capcitySum(facilitiesByHubId, h.id, BICYCLE)),
                              col("Polkupyörä, lukittu tila", h -> capcitySum(facilitiesByHubId, h.id, BICYCLE_SECURE_SPACE)),
                              col("Pysäköintipaikat", h -> facilitiesByHubId.get(h.id).stream().map((Facility f) -> f.name.fi).collect(toList()))));
    }

    private int capcitySum(Map<Long, List<Facility>> facilitiesByHubId, long hubId, CapacityType... types) {
        List<Facility> facilities = facilitiesByHubId.get(hubId);
        int sum = 0;
        for (CapacityType type : types) {
          sum += facilities.stream().mapToInt((Facility f) -> f.builtCapacity.get(type)).sum();
        }
        return sum;
    }

    private List<Hub> getHubs() {
        HubSearch search = new HubSearch();
        search.setLimit(10000);
        return hubService.search(search).results;
    }

    private List<Facility> getFacilities() {
        PageableFacilitySearch search = new PageableFacilitySearch();
        search.setLimit(10000);
        List<FacilityInfo> facilityInfos = facilityService.search(search).results;
        List<Facility> facilities = facilityInfos.stream().map((FacilityInfo f) -> facilityService.getFacility(f.id))
                                                 .collect(toList());
        return facilities;
    }

    private static int capacitySum(Map<CapacityType, Integer> capacityValues, List<CapacityType> capacityTypes) {
        return capacityTypes.stream().map(capacityValues::get).filter((n) -> n != null).mapToInt((n) -> n).sum();
    }

    private static String time(TimeDuration time) {
        return format("%02d:%02d - %02d:%02d", time.from.getHour(), time.from.getMinute(), time.until.getHour(),
                      time.until.getMinute());
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
