package fi.hsl.parkandride.adapter.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import fi.hsl.parkandride.core.port.event.parkingarea.CreateParkingAreaEvent;
import fi.hsl.parkandride.core.port.event.parkingarea.ParkingAreaCreatedEvent;
import fi.hsl.parkandride.core.service.ParkingAreaService;
import fi.hsl.parkandride.adapter.rest.domain.ParkingArea;

@RestController
@RequestMapping("/parking-areas")
public class ParkingAreaCommandController {
    @Autowired
    private ParkingAreaService parkingAreaService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ParkingArea> createParkingArea(@RequestBody ParkingArea parkingArea, UriComponentsBuilder builder) {
        ParkingAreaCreatedEvent e = parkingAreaService.createParkingArea(new CreateParkingAreaEvent(parkingArea.toApplicationDomain()));
        ParkingArea newParkingArea = ParkingArea.fromApplicationDomain(e.parkingArea);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/parking-areas/{id}").buildAndExpand(newParkingArea.getParkingAreaId().toString()).toUri());
        return new ResponseEntity<ParkingArea>(newParkingArea, headers, HttpStatus.CREATED);
    }
}
