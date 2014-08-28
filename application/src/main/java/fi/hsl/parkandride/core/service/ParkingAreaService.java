package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.port.event.parkingarea.AllParkingAreasEvent;
import fi.hsl.parkandride.core.port.event.parkingarea.CreateParkingAreaEvent;
import fi.hsl.parkandride.core.port.event.parkingarea.ParkingAreaCreatedEvent;
import fi.hsl.parkandride.core.port.event.parkingarea.RequestAllParkingAreasEvent;
import fi.hsl.parkandride.core.port.repository.ParkingAreaRepository;

import com.google.common.collect.Lists;

public class ParkingAreaService {
    private ParkingAreaRepository repository;

    public ParkingAreaService(ParkingAreaRepository repository) {
        this.repository = repository;
    }

    public ParkingAreaCreatedEvent createParkingArea(CreateParkingAreaEvent e) {
        return new ParkingAreaCreatedEvent(repository.save(e.parkingArea));
    }

    public AllParkingAreasEvent requestAllParkingAreas(RequestAllParkingAreasEvent e) {
        return new AllParkingAreasEvent(Lists.newArrayList(repository.findAll()));
    }
}
