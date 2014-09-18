package fi.hsl.parkandride.core.outbound;

import java.util.List;

import fi.hsl.parkandride.core.domain.Facility;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;

public interface FacilityRepository {

    long insertFacility(Facility facility);

    void updateFacility(Facility facility);

    void updateFacility(Facility newFacility, Facility oldFacility);

    Facility getFacility(long id);

    Facility getFacilityForUpdate(long id);

    List<Facility> findFacilities(FacilitySearch search);

}
