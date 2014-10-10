package fi.hsl.parkandride.core.outbound;

import fi.hsl.parkandride.core.domain.Facility;
import fi.hsl.parkandride.core.domain.FacilitySummary;
import fi.hsl.parkandride.core.domain.PageableSpatialSearch;
import fi.hsl.parkandride.core.domain.SpatialSearch;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.service.TransactionalRead;

public interface FacilityRepository {

    long insertFacility(Facility facility);

    void updateFacility(long facilityId, Facility facility);

    void updateFacility(long facilityId, Facility newFacility, Facility oldFacility);

    Facility getFacility(long id);

    Facility getFacilityForUpdate(long id);

    SearchResults<Facility> findFacilities(PageableSpatialSearch search);

    FacilitySummary summarizeFacilities(SpatialSearch search);
}
