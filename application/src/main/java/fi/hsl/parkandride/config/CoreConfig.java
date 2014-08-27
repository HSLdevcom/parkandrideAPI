package fi.hsl.parkandride.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fi.hsl.parkandride.adapter.repository.ParkingAreaMemoryRepository;
import fi.hsl.parkandride.core.port.repository.ParkingAreaRepository;
import fi.hsl.parkandride.core.service.ParkingAreaService;

@Configuration
public class CoreConfig {

    @Bean
    public ParkingAreaService parkingAreaService(ParkingAreaRepository parkingAreaRepository) {
        return new ParkingAreaService(parkingAreaRepository);
    }

    @Bean
    public ParkingAreaRepository parkingAreaRepository() {
        return new ParkingAreaMemoryRepository();
    }
}
