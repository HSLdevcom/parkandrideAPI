package fi.hsl.parkandride.core.service.reporting;

import fi.hsl.parkandride.core.domain.*;
import org.geolatte.geom.Geometry;
import java.util.*;
import static java.util.Collections.singletonMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

final class ReportContext {
    final Map<Long, Facility> facilities;
    final Map<Long, Hub> hubs;
    final Map<Long, Operator> operators;
    final Map<Long, List<Facility>> facilitiesByHubId;
    final Map<Long, List<Long>> facilityIdsByOperatorId;
    final Map<Long, List<Hub>> hubsByFacilityId;
    final Map<Long, Region> regionByFacilityId;
    final Map<Long, Region> regionByHubId;
    final Long allowedOperatorId;
    final Collection<Region> regions;

    public ReportContext(ReportServiceSupport reportService, Long allowedOperatorId) {
        this.allowedOperatorId = allowedOperatorId;
        facilities = getFacilities(reportService, allowedOperatorId);
        hubs = getHubs(reportService);
        operators = getOperators(reportService, allowedOperatorId);
        facilitiesByHubId = new HashMap<>();
        hubsByFacilityId = new HashMap<>();
        regionByHubId = new HashMap<>();
        regionByFacilityId = new HashMap<>();
        regions = reportService.regionRepository.getRegions();
        hubs.values().forEach(hub -> {
            List<Facility> hubFacilities = hub.facilityIds.stream().map(id -> facilities.get(id)).filter(f -> f != null).collect(toList());
            facilitiesByHubId.put(hub.id, hubFacilities);
            hubsToFacilities(hub, hubFacilities, hubsByFacilityId);
            regionByHubId.put(hub.id, getRegion(hub.location));
        });
        facilities.values().forEach(facility -> {
            regionByFacilityId.put(facility.id, getRegion(facility.location));
        });
        facilityIdsByOperatorId = facilities.values().stream().collect(groupingBy(f -> f.operatorId, mapping(f -> f.id, toList())));
    }

    private Region getRegion(Geometry location) {
        for (Region r : regions) {
            if (r.area.intersects(location)) {
                return r;
            }
        };
        return Region.UNKNOWN_REGION;
    }

    private Map<Long, Hub> getHubs(ReportServiceSupport reportService) {
        HubSearch search = new HubSearch();
        search.setLimit(10000);
        List<Hub> hubs = reportService.hubService.search(search).results;
        return hubs.stream().collect(toMap((Hub h) -> h.id, identity(), (u, v) -> u, LinkedHashMap::new));
    }

    private Map<Long, Facility> getFacilities(ReportServiceSupport reportService, Long allowedOperatorId) {
        PageableFacilitySearch search = new PageableFacilitySearch();
        search.setLimit(10000);
        search.setOperatorId(allowedOperatorId);
        List<FacilityInfo> facilityInfos = reportService.facilityService.search(search).results;
        return facilityInfos.stream().map((FacilityInfo f) -> reportService.facilityService.getFacility(f.id)).collect(toMap((Facility f) -> f.id, identity(), (u, v) -> u, LinkedHashMap::new));
    }

    private Map<Long, Operator> getOperators(ReportServiceSupport reportService, Long allowedOperatorId) {
        if (allowedOperatorId != null) {
            return singletonMap(allowedOperatorId, reportService.operatorService.getOperator(allowedOperatorId));
        }
        OperatorSearch search = new OperatorSearch();
        search.setLimit(10000);
        List<Operator> operators = reportService.operatorService.search(search).results;
        return operators.stream().collect(toMap((Operator o) -> o.id, identity(), (u, v) -> u, LinkedHashMap::new));
    }

    private void hubsToFacilities(Hub hub, List<Facility> hubFacilities, Map<Long, List<Hub>> hubsByFacilityId) {
        for (Facility facility : hubFacilities) {
            List<Hub> hubList = hubsByFacilityId.get(facility.id);
            if (hubList == null) {
                hubList = new ArrayList<>();
                hubsByFacilityId.put(facility.id, hubList);
            }
            hubList.add(hub);
        }
    }
}