package fi.hsl.parkandride.core.domain;

import java.util.Set;

public class PageableSpatialSearch extends SpatialSearch {

    public int limit = 100;

    public long offset = 0l;

    public Set<Long> ids;

}
