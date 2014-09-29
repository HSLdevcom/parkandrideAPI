package fi.hsl.parkandride.core.domain;

import java.util.Set;

import org.geolatte.geom.Geometry;

public class Hub {

    public Long id;

    public String name;

    public Geometry location;

    public Set<Long> facilityIds;

}
