package fi.hsl.parkandride.adapter.rest.controller.fixture;

import fi.hsl.parkandride.application.port.event.parkingarea.ParkingAreaCreatedEvent;

public abstract class RestEventFixture {
    public static ParkingAreaCreatedEvent parkingAreaCreated(Long id) { return new ParkingAreaCreatedEvent(RestDataFixture.customIdParkingArea(id)); }
}
