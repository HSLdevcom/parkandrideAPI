package fi.hsl.parkandride.adapter.rest.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fi.hsl.parkandride.adapter.rest.domain.ParkingArea;
import fi.hsl.parkandride.core.port.event.parkingarea.RequestAllParkingAreasEvent;
import fi.hsl.parkandride.core.service.ParkingAreaService;

@RestController
@RequestMapping("/parking-areas")
public class ParkingAreaQueryController {
    @Autowired
    private ParkingAreaService parkingAreaService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ParkingArea> getAllOrders() {
        return parkingAreaService.requestAllParkingAreas(new RequestAllParkingAreasEvent()).parkingAreas.stream()
                .map(v -> ParkingArea.fromCoreDomain(v))
                .collect(Collectors.toList());
    }
}
