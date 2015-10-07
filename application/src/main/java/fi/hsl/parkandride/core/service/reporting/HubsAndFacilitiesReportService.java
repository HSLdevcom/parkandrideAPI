// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service.reporting;

import fi.hsl.parkandride.back.RegionRepository;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.*;
import fi.hsl.parkandride.front.ReportParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static fi.hsl.parkandride.core.domain.CapacityType.*;
import static fi.hsl.parkandride.core.domain.DayType.*;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class HubsAndFacilitiesReportService extends AbstractReportService {

    private static final String REPORT_NAME = "HubsAndFacilities";

    public HubsAndFacilitiesReportService(FacilityService facilityService, OperatorService operatorService, ContactService contactService, HubService hubService,
                                          UtilizationRepository utilizationRepository, RegionRepository regionRepository, TranslationService translationService) {
        super(REPORT_NAME, facilityService, operatorService, contactService, hubService, utilizationRepository, translationService, regionRepository);
    }

    @Override
    protected Excel generateReport(ReportContext ctx, ReportParameters params) {
        Excel excel = new Excel();
        addRegionsSheet(excel, new ArrayList<>(ctx.hubs.values()), ctx);
        addFacilitiesSheet(excel, new ArrayList<>(ctx.facilities.values()), ctx);
        excel.addSheet(excelUtil.getMessage("reports.hf.sheets.legend"),
                excelUtil.getMessage("reports.hf.legend").split("\n"));
        return excel;
    }

    private void addFacilitiesSheet(Excel excel, List<Facility> facilities, ReportContext ctx) {

        excel.addSheet(excelUtil.getMessage("reports.hf.sheets.facilities"), facilities, asList(
                excelUtil.tcol("reports.hf.facility.facilityName", (Facility f) -> f.name),
                excelUtil.tcol("reports.hf.facility.aliases", (Facility f) -> join(", ", f.aliases)),
                excelUtil.tcol("reports.hf.facility.hubs", (Facility f) -> ctx.hubsByFacilityId.getOrDefault(f.id, emptyList()).stream().map((Hub h) -> h.name.fi).collect(joining(", "))),
                excelUtil.tcol("reports.hf.facility.operator", (Facility f) -> ctx.operators.get(f.operatorId).name),
                excelUtil.tcol("reports.hf.facility.status", (Facility f) -> translationService.translate(f.status)),
                excelUtil.tcol("reports.hf.facility.statusDescription", (Facility f) -> f.statusDescription),
                excelUtil.tcol("reports.hf.facility.locX", (Facility f) -> f.location.getCentroid().getX()),
                excelUtil.tcol("reports.hf.facility.locY", (Facility f) -> f.location.getCentroid().getY()),
                excelUtil.tcol("reports.hf.facility.openingHoursbusinessDay", (Facility f) -> ExcelUtil.time(f.openingHours.byDayType.get(BUSINESS_DAY))),
                excelUtil.tcol("reports.hf.facility.openingHoursSaturday", (Facility f) -> ExcelUtil.time(f.openingHours.byDayType.get(SATURDAY))),
                excelUtil.tcol("reports.hf.facility.openingHoursSunday", (Facility f) -> ExcelUtil.time(f.openingHours.byDayType.get(SUNDAY))),
                excelUtil.tcol("reports.hf.facility.openingHoursInfo", (Facility f) -> f.openingHours.info),
                excelUtil.tcol("reports.hf.facility.motorCapacity", (Facility f) -> capacitySum(f.builtCapacity, motorCapacityList)),
                excelUtil.tcol("reports.hf.facility.bicycleCapacity", (Facility f) -> capacitySum(f.builtCapacity, bicycleCapacityList)),
                excelUtil.tcol("reports.hf.facility.car", (Facility f) -> f.builtCapacity.getOrDefault(CAR, 0)),
                excelUtil.tcol("reports.hf.facility.disabled", (Facility f) -> f.builtCapacity.getOrDefault(DISABLED, 0)),
                excelUtil.tcol("reports.hf.facility.electricCar", (Facility f) -> f.builtCapacity.getOrDefault(ELECTRIC_CAR, 0)),
                excelUtil.tcol("reports.hf.facility.motorcycle", (Facility f) -> f.builtCapacity.getOrDefault(MOTORCYCLE, 0)),
                excelUtil.tcol("reports.hf.facility.bicycle", (Facility f) -> f.builtCapacity.getOrDefault(BICYCLE, 0)),
                excelUtil.tcol("reports.hf.facility.bicycleSecure", (Facility f) -> f.builtCapacity.getOrDefault(BICYCLE_SECURE_SPACE, 0)),
                excelUtil.tcol("reports.hf.facility.pricing", (Facility f) -> f.pricing.stream().map((p) -> translationService.translate(p.usage)).distinct().collect(joining(", "))),
                excelUtil.tcol("reports.hf.facility.paymentMethod", (Facility f) -> f.paymentInfo.paymentMethods.stream().map(m -> translationService.translate(m)).collect(joining(", "))),
                excelUtil.tcol("reports.hf.facility.paymentMethodInfo", (Facility f) -> f.paymentInfo.detail),
                excelUtil.tcol("reports.hf.facility.services", (Facility f) -> f.services.stream().map(s -> translationService.translate(s)).collect(joining(", "))),
                excelUtil.tcol("reports.hf.facility.emergencyContact", (Facility f) -> contactText(f.contacts.emergency)),
                excelUtil.tcol("reports.hf.facility.operatorContact", (Facility f) -> contactText(f.contacts.operator)),
                excelUtil.tcol("reports.hf.facility.serviceContact", (Facility f) -> contactText(f.contacts.service))
        ));

    }

    private void addRegionsSheet(Excel excel, List<Hub> hubs, ReportContext ctx) {
        excel.addSheet(excelUtil.getMessage("reports.hf.sheets.hubs"), hubs, asList(
                excelUtil.tcol("reports.hf.hub.name", (Hub h) -> h.name),
                excelUtil.tcol("reports.hf.hub.address", (Hub h) -> addressText(h.address)),
                excelUtil.tcol("reports.hf.hub.locX", (Hub h) -> h.location.getX()),
                excelUtil.tcol("reports.hf.hub.locY", (Hub h) -> h.location.getY()),
                excelUtil.tcol("reports.hf.hub.motorCapacity", (Hub h) -> capacitySum(ctx, h.id, motorCapacities)),
                excelUtil.tcol("reports.hf.hub.bicycleCapacity", (Hub h) -> capacitySum(ctx, h.id, bicycleCapacities)),
                excelUtil.tcol("reports.hf.hub.car", (Hub h) -> capacitySum(ctx, h.id, CAR)),
                excelUtil.tcol("reports.hf.hub.disabled", (Hub h) -> capacitySum(ctx, h.id, DISABLED)),
                excelUtil.tcol("reports.hf.hub.electicCar", (Hub h) -> capacitySum(ctx, h.id, ELECTRIC_CAR)),
                excelUtil.tcol("reports.hf.hub.motorcycle", (Hub h) -> capacitySum(ctx, h.id, MOTORCYCLE)),
                excelUtil.tcol("reports.hf.hub.bicycle", (Hub h) -> capacitySum(ctx, h.id, BICYCLE)),
                excelUtil.tcol("reports.hf.hub.bicycleSecure", (Hub h) -> capacitySum(ctx, h.id, BICYCLE_SECURE_SPACE)),
                excelUtil.tcol("reports.hf.hub.facilities", (Hub h) -> ctx.facilitiesByHubId.getOrDefault(h.id, emptyList()).stream().map((Facility f) -> f.name.fi).collect(toList()))
        ));
    }

    private static <T> StringBuilder appendIfNotNull(StringBuilder sb, T toAppend, Function<T, Object> fn, boolean separator) {
        if (toAppend != null) {
            if (separator) {
                sb.append(", ");
            }
            sb.append(fn.apply(toAppend));
        }
        return sb;
    }

    private static <T> StringBuilder appendIfNotNull(StringBuilder sb, T toAppend, Function<T, Object> fn) {
        return appendIfNotNull(sb, toAppend, fn, true);
    }

    private static <T> StringBuilder appendIfNotNull(StringBuilder sb, T toAppend) {
        return appendIfNotNull(sb, toAppend, a -> a);
    }

    private static CharSequence addressText(Address address) {
        if (address == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        MultilingualString street = address.getStreetAddress();
        appendIfNotNull(sb, street, st -> st.fi, false);
        appendIfNotNull(sb, address.postalCode);
        appendIfNotNull(sb, address.city, city -> city.fi);
        return sb;
    }

    private CharSequence contactText(Long contactId) {
        if (contactId == null) {
            return null;
        }
        Contact contact = contactService.getContact(contactId);

        StringBuilder sb = new StringBuilder();
        sb.append(contact.name.fi);
        appendIfNotNull(sb, contact.phone);
        appendIfNotNull(sb, contact.email);
        appendIfNotNull(sb, contact.address, addr -> addressText(addr));
        appendIfNotNull(sb, contact.openingHours, oh -> oh.fi);
        appendIfNotNull(sb, contact.info, info -> info.fi);
        return sb;
    }

    private int capacitySum(ReportContext ctx, long hubId, CapacityType... types) {
        List<Facility> facilities = ctx.facilitiesByHubId.getOrDefault(hubId, emptyList());
        return facilities.stream()
                .mapToInt(f -> capacitySum(f.builtCapacity, asList(types)))
                .sum();
    }

    private static int capacitySum(Map<CapacityType, Integer> capacityValues, List<CapacityType> capacityTypes) {
        return capacityTypes.stream().mapToInt(type -> capacityValues.getOrDefault(type, 0)).sum();
    }
}
