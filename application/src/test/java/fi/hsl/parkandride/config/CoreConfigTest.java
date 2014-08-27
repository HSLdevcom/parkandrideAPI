package fi.hsl.parkandride.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hsl.parkandride.core.domain.ParkingArea;
import fi.hsl.parkandride.core.port.event.parkingarea.CreateParkingAreaEvent;
import fi.hsl.parkandride.core.port.event.parkingarea.RequestAllParkingAreasEvent;
import fi.hsl.parkandride.core.service.ParkingAreaService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfig.class})
public class CoreConfigTest {
    @Autowired
    private ParkingAreaService parkingAreaService;

    @Test
    public void add_parking_area() {
        assertThat(parkingAreaService.requestAllParkingAreas(new RequestAllParkingAreasEvent()).parkingAreas.isEmpty());

        CreateParkingAreaEvent e = new CreateParkingAreaEvent(new ParkingArea());
        parkingAreaService.createParkingArea(e);

        assertThat(parkingAreaService.requestAllParkingAreas(new RequestAllParkingAreasEvent()).parkingAreas.size()).isEqualTo(1);
    }
}
