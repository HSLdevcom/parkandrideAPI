package fi.hsl.parkandride.core.domain;

import java.util.Set;

import org.geolatte.geom.Geometry;

public class SpatialSearch {

    public Geometry intersecting;

    public Set<Long> ids;

    public Sort sort;

}
