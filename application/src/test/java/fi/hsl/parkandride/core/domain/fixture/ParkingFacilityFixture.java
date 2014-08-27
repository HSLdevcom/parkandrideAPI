package fi.hsl.parkandride.core.domain.fixture;

import java.util.Map;

import fi.hsl.parkandride.core.domain.MultiLingualString;
import fi.hsl.parkandride.core.domain.ParkingFacility;
import fi.hsl.parkandride.core.domain.ParkingProperties;

public class ParkingFacilityFixture {
    public interface ParkingFacilityId {
        long _1 = 1L;
    }

    public static ParkingFacility ruoholahti() {
        ParkingFacility parkingFacility = new ParkingFacility();
        parkingFacility.setId(ParkingFacilityId._1);
        parkingFacility.setName(new MultiLingualString(
                "Ruoholahden kauppakeskus, pysäköintihalli",
                "Köpcentrum i Helsingfors Ruoholahti garage",
                "Shopping center in Helsinki Ruoholahti garage"));
        parkingFacility.setProperties(new ParkingProperties(new MultiLingualString(
                "Ruoholahden kauppakeskus, kallioluola",
                "Ruoholahtis köpcentrum i berget grottan",
                "Ruoholahti's shopping center in the rock cave")));
        return parkingFacility;
    }

    public static Map<Long, ParkingFacility> parkingFacilities() {
        return FixtureUtil.mapById(ruoholahti());
    }
}
