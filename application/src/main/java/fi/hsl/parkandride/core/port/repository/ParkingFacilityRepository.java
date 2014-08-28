package fi.hsl.parkandride.core.port.repository;

import org.springframework.data.repository.CrudRepository;

import fi.hsl.parkandride.core.domain.ParkingFacility;

public interface ParkingFacilityRepository extends CrudRepository<ParkingFacility, Long> {
}
