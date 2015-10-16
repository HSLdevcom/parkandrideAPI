// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

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
