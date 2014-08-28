package fi.hsl.parkandride.core.port.repository;

import org.springframework.data.repository.CrudRepository;

import fi.hsl.parkandride.core.domain.ParkingArea;

public interface ParkingAreaRepository extends CrudRepository<ParkingArea, Long> {
}
