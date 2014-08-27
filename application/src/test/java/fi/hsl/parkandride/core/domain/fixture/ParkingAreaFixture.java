package fi.hsl.parkandride.core.domain.fixture;

import java.util.Map;

import fi.hsl.parkandride.core.domain.MultiLingualString;
import fi.hsl.parkandride.core.domain.ParkingArea;
import fi.hsl.parkandride.core.domain.ParkingProperties;

public abstract class ParkingAreaFixture {
    public interface ParkingAreaId {
        long _1 = 1L;
    }

    public static ParkingArea ruoholahti() {
        ParkingArea parkingArea = new ParkingArea();
        parkingArea.setId(ParkingAreaId._1);
        parkingArea.setName(new MultiLingualString(
                "P-Ruoholahti, Helsinki",
                "P-Gräsviken, Helsingfors",
                "P-Ruoholahti, Helsinki"));
        parkingArea.setProperties(new ParkingProperties(new MultiLingualString(
                "Ruoholahden kauppakeskus",
                "Gräsvikens köpcentrum",
                "Ruoholahti's shopping center")));
        return parkingArea;
    };

    public static Map<Long, ParkingArea> parkingAreas() {
        return FixtureUtil.mapById(ruoholahti());
    }
}
