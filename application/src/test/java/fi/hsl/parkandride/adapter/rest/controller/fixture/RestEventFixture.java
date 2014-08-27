package fi.hsl.parkandride.adapter.rest.controller.fixture;

import fi.hsl.parkandride.core.port.event.parkingarea.ParkingAreaCreatedEvent;

public abstract class RestEventFixture {
    public static ParkingAreaCreatedEvent parkingAreaCreated(Long id) { return new ParkingAreaCreatedEvent(RestDataFixture.customIdParkingArea(id)); }
}
