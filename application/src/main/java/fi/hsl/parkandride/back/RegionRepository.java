package fi.hsl.parkandride.back;

import fi.hsl.parkandride.core.domain.Region;
import java.util.Collection;

public interface RegionRepository {

    Collection<Region> getRegions();

}
