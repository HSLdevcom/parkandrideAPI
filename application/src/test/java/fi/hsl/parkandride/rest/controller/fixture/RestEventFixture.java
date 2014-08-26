package fi.hsl.parkandride.rest.controller.fixture;

import fi.hsl.parkandride.application.event.parkingarea.ParkingAreaCreatedEvent;

public abstract class RestEventFixture {
    public static ParkingAreaCreatedEvent parkingAreaCreated(Long id) { return new ParkingAreaCreatedEvent(RestDataFixture.customIdParkingArea(id)); }
}
