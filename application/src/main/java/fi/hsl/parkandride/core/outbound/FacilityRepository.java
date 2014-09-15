package fi.hsl.parkandride.core.outbound;

import java.util.List;

import fi.hsl.parkandride.core.domain.Facility;

public interface FacilityRepository {

    long insertFacility(Facility facility);

    void updateFacility(Facility facility);

    Facility getFacility(long id);

    List<Facility> findFacilities(FacilitySearch search);

}
