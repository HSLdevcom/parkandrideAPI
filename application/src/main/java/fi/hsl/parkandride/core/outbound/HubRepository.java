package fi.hsl.parkandride.core.outbound;

import java.util.List;

import fi.hsl.parkandride.core.domain.Hub;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.SpatialSearch;

public interface HubRepository {

    long insertHub(Hub hub);

    void updateHub(long hubId, Hub hub);

    Hub getHub(long hubId);

    List<Hub> findHubs(SpatialSearch search);

}
