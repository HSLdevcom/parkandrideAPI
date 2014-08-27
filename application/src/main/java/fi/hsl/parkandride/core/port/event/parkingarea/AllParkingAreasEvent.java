package fi.hsl.parkandride.core.port.event.parkingarea;

import java.util.Collection;
import java.util.Collections;

import fi.hsl.parkandride.core.domain.ParkingArea;
import fi.hsl.parkandride.core.port.event.RequestReadEvent;

public class AllParkingAreasEvent extends RequestReadEvent {
    public final Collection<ParkingArea> parkingAreas;

    public AllParkingAreasEvent(Collection<ParkingArea> parkingAreas) {
        this.parkingAreas = Collections.unmodifiableCollection(parkingAreas);
    }
}
