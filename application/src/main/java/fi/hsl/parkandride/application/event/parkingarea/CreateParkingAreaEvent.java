package fi.hsl.parkandride.application.event.parkingarea;

import fi.hsl.parkandride.application.domain.ParkingArea;

public class CreateParkingAreaEvent {
    public final ParkingArea parkingArea;

    public CreateParkingAreaEvent(ParkingArea parkingArea) {
        this.parkingArea = parkingArea;
    }
}
