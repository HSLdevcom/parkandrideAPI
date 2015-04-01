// Copyright © 2015 HSL

package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.*;

public interface FacilityRepository {

    long insertFacility(Facility facility);

    void updateFacility(long facilityId, Facility facility);

    void updateFacility(long facilityId, Facility newFacility, Facility oldFacility);

    Facility getFacility(long facilityId);

    FacilityInfo getFacilityInfo(long facilityId);

    Facility getFacilityForUpdate(long facilityId);

    SearchResults<FacilityInfo> findFacilities(PageableFacilitySearch search);

    FacilitySummary summarizeFacilities(FacilitySearch search);
}
