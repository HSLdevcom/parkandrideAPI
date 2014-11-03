package fi.hsl.parkandride.core.domain;

public class PageableSpatialSearch extends SpatialSearch {

    public int limit = 100;

    public long offset = 0l;

    public Sort sort;

}
