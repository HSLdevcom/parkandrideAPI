package fi.hsl.parkandride.adapter.rest.controller.fixture;

import fi.hsl.parkandride.core.domain.ParkingArea;

public abstract class RestDataFixture {
    public static ParkingArea customIdParkingArea(Long id) {
        ParkingArea parkingArea = new ParkingArea();
        parkingArea.setId(id);
        return parkingArea;
    }

    public static String defaultParkingAreaJSON() {
        return "{\"parkingAreaName\": {\"fi-FI\": \"P-Ruoholahti, Helsinki\", \"se-SE\": \"P-Gräsviken, Helsingfors\", \"en-EN\": \"P-Ruoholahti, Helsinki\"}}";
    }
}
