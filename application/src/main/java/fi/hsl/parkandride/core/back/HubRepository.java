// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

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
