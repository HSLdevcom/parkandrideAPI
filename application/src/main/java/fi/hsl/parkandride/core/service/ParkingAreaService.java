package fi.hsl.parkandride.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hsl.parkandride.core.port.event.parkingarea.CreateParkingAreaEvent;
import fi.hsl.parkandride.core.port.event.parkingarea.ParkingAreaCreatedEvent;
import fi.hsl.parkandride.core.port.repository.ParkingAreaRepository;

@Service
public class ParkingAreaService {
    private ParkingAreaRepository repository;

    @Autowired
    public ParkingAreaService(ParkingAreaRepository repository) {
        this.repository = repository;
    }

    public ParkingAreaCreatedEvent createParkingArea(CreateParkingAreaEvent e) {
        return new ParkingAreaCreatedEvent(repository.save(e.parkingArea));
    }
}
