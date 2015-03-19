// Copyright © 2015 HSL

package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.Hub;
import fi.hsl.parkandride.core.domain.HubSearch;
import fi.hsl.parkandride.core.domain.SearchResults;

public interface HubRepository {

    long insertHub(Hub hub);

    void updateHub(long hubId, Hub hub);

    Hub getHub(long hubId);

    SearchResults<Hub> findHubs(HubSearch search);

}
