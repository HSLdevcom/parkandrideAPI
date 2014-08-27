package fi.hsl.parkandride.application.port.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import fi.hsl.parkandride.application.domain.ParkingFacility;
import fi.hsl.parkandride.application.domain.fixture.ParkingFacilityFixture;
import fi.hsl.parkandride.adapter.repository.ParkingFacilityMemoryRepository;

public class ParkingFacilityRepositoryTest {
    private final ParkingFacilityRepository parkingFacilityRepository = new ParkingFacilityMemoryRepository();

    @Before
    public void setup() {
        ((ParkingFacilityMemoryRepository)parkingFacilityRepository).reset(ParkingFacilityFixture.parkingFacilities());
    }

    @Test
    public void find_parking_facilities() {
        assertThat(parkingFacilityRepository.findAll()).isNotEmpty();
    }

    @Test
    public void find_parking_facility() {
        ParkingFacility expected = ParkingFacilityFixture.ruoholahti();
        assertThat(parkingFacilityRepository.findOne(expected.getId()).getId()).isEqualTo(expected.getId());
    }

    @Test
    public void save_parking_facility() {
        assertThat(parkingFacilityRepository.findAll()).isNotEmpty();

        parkingFacilityRepository.delete(ParkingFacilityFixture.ruoholahti().getId());
        assertThat(parkingFacilityRepository.findAll()).isEmpty();

        parkingFacilityRepository.save(ParkingFacilityFixture.ruoholahti());
        assertThat(parkingFacilityRepository.findAll()).isNotEmpty();
    }
}
