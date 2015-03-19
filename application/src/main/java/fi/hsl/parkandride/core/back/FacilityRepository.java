// Copyright © 2015 HSL

package fi.hsl.parkandride.core.back;

import java.util.List;

import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.TransactionalRead;

public interface FacilityRepository {

    long insertFacility(Facility facility);

    void updateFacility(long facilityId, Facility facility);

    void updateFacility(long facilityId, Facility newFacility, Facility oldFacility);

    Facility getFacility(long id);

    FacilityInfo getFacilityInfo(long facilityId);

    Facility getFacilityForUpdate(long id);

    SearchResults<FacilityInfo> findFacilities(PageableFacilitySearch search);

    FacilitySummary summarizeFacilities(FacilitySearch search);

    void insertUtilization(long facilityId, List<Utilization> statuses);

    List<Utilization> getStatuses(long facilityId);

}
