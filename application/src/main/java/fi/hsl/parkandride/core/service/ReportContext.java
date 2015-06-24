package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.domain.*;
import java.util.*;
import static java.util.Collections.singletonMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

class ReportContext {
    Map<Long, Facility> facilities;
    Map<Long, Hub> hubs;
    Map<Long, Operator> operators;
    Map<Long, List<Facility>> facilitiesByHubId;
    Map<Long, List<Long>> facilityIdsByOperatorId;
    Map<Long, List<Hub>> hubsByFacilityId;
    Long allowedOperatorId;

    public ReportContext(ReportService reportService, Long allowedOperatorId) {
        this.allowedOperatorId = allowedOperatorId;
        facilities = getFacilities(reportService, allowedOperatorId);
        hubs = getHubs(reportService);
        operators = getOperators(reportService, allowedOperatorId);
        facilitiesByHubId = new HashMap<>();
        hubsByFacilityId = new HashMap<>();
        hubs.values().stream().forEach(hub -> {
            List<Facility> hubFacilities = hub.facilityIds.stream().map(id -> facilities.get(id)).filter(f -> f != null).collect(toList());
            facilitiesByHubId.put(hub.id, hubFacilities);
            hubsToFacilities(hub, hubFacilities, hubsByFacilityId);
        });
        facilityIdsByOperatorId = facilities.values().stream().collect(groupingBy(f -> f.operatorId, mapping(f -> f.id, toList())));
    }

    private Map<Long, Hub> getHubs(ReportService reportService) {
        HubSearch search = new HubSearch();
        search.setLimit(10000);
        List<Hub> hubs = reportService.hubService.search(search).results;
        return hubs.stream().collect(toMap((Hub h) -> h.id, identity(), (u, v) -> u, LinkedHashMap::new));
    }

    private Map<Long, Facility> getFacilities(ReportService reportService, Long allowedOperatorId) {
        PageableFacilitySearch search = new PageableFacilitySearch();
        search.setLimit(10000);
        search.setOperatorId(allowedOperatorId);
        List<FacilityInfo> facilityInfos = reportService.facilityService.search(search).results;
        return facilityInfos.stream().map((FacilityInfo f) -> reportService.facilityService.getFacility(f.id)).collect(toMap((Facility f) -> f.id, identity(), (u, v) -> u, LinkedHashMap::new));
    }

    private Map<Long, Operator> getOperators(ReportService reportService, Long allowedOperatorId) {
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