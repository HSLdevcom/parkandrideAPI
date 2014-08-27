package fi.hsl.parkandride.core.port.event.parkingarea;

import fi.hsl.parkandride.core.domain.ParkingArea;

public class ParkingAreaCreatedEvent {
    public final ParkingArea parkingArea;

    public ParkingAreaCreatedEvent(ParkingArea parkingArea) {
        this.parkingArea = parkingArea;
    }
}
