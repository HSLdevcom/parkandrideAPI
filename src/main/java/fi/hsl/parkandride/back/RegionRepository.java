package fi.hsl.parkandride.back;

import fi.hsl.parkandride.core.domain.Region;
import fi.hsl.parkandride.core.domain.RegionWithHubs;

import java.util.Collection;

public interface RegionRepository {

    Collection<Region> getRegions();

    Collection<RegionWithHubs> regionsWithHubs();
}
