package fi.hsl.parkandride.application.port.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import fi.hsl.parkandride.application.domain.ParkingArea;
import fi.hsl.parkandride.application.domain.fixture.ParkingAreaFixture;
import fi.hsl.parkandride.adapter.repository.ParkingAreaMemoryRepository;

public class ParkingAreaRepositoryTest {
    private final ParkingAreaRepository parkingAreaRepository = new ParkingAreaMemoryRepository();

    @Before
    public void setup() {
        ((ParkingAreaMemoryRepository)parkingAreaRepository).reset(ParkingAreaFixture.parkingAreas());
    }

    @Test
    public void find_parking_areas() {
        assertThat(parkingAreaRepository.findAll()).isNotEmpty();
    }

    @Test
    public void find_parking_area() {
        ParkingArea expected = ParkingAreaFixture.ruoholahti();
        assertThat(parkingAreaRepository.findOne(expected.getId()).getId()).isEqualTo(expected.getId());
    }

    @Test
    public void save_parking_area() {
        assertThat(parkingAreaRepository.findAll()).isNotEmpty();

        parkingAreaRepository.delete(ParkingAreaFixture.ruoholahti().getId());
        assertThat(parkingAreaRepository.findAll()).isEmpty();

        parkingAreaRepository.save(ParkingAreaFixture.ruoholahti());
        assertThat(parkingAreaRepository.findAll()).isNotEmpty();
    }
}