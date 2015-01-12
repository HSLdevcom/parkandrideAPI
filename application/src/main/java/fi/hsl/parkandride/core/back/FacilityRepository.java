package fi.hsl.parkandride.core.back;

import java.util.List;

import fi.hsl.parkandride.core.domain.*;

public interface FacilityRepository {

    long insertFacility(Facility facility);

    void updateFacility(long facilityId, Facility facility);

    void updateFacility(long facilityId, Facility newFacility, Facility oldFacility);

    Facility getFacility(long id);

    Facility getFacilityForUpdate(long id);

    SearchResults<Facility> findFacilities(PageableSpatialSearch search);

    void insertStatuses(long facilityId, List<FacilityStatus> statuses);

    List<FacilityStatus> getStatuses(long facilityId);
}
