// Copyright © 2015 HSL

package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.*;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Set;

public interface FacilityRepository {

    long insertFacility(Facility facility);

    void updateFacility(long facilityId, Facility facility);

    void updateFacility(long facilityId, Facility newFacility, Facility oldFacility);

    Facility getFacility(long facilityId);

    FacilityInfo getFacilityInfo(long facilityId);

    Facility getFacilityForUpdate(long facilityId);

    SearchResults<FacilityInfo> findFacilities(PageableFacilitySearch search);

    FacilitySummary summarizeFacilities(FacilitySearch search);

    void insertUtilizations(List<Utilization> utilizations);

    Set<Utilization> findLatestUtilization(long facilityId);

    List<Utilization> findUtilizationsBetween(UtilizationKey utilizationKey, DateTime start, DateTime end);
}
