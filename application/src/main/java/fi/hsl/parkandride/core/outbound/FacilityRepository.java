package fi.hsl.parkandride.core.outbound;

import fi.hsl.parkandride.core.domain.Facility;

public interface FacilityRepository {
    long insertFacility(Facility facility);
}
