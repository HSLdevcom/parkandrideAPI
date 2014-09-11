package fi.hsl.parkandride.core.outbound;

import fi.hsl.parkandride.core.domain.Facility;
import fi.hsl.parkandride.outbound.TransactionalWrite;

public interface FacilityRepository {
    long insertFacility(Facility facility);
}
