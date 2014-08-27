package fi.hsl.parkandride.core.port.event.parkingarea;

import fi.hsl.parkandride.core.domain.ParkingArea;

public class CreateParkingAreaEvent {
    public final ParkingArea parkingArea;

    public CreateParkingAreaEvent(ParkingArea parkingArea) {
        this.parkingArea = parkingArea;
    }
}
