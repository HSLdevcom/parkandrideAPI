package fi.hsl.parkandride.core.domain;

import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.*;

import org.geolatte.geom.Geometry;

import com.google.common.collect.Sets;

public class Facility {

    public Long id;

    public String name;

    public Geometry border;

    public Set<String> aliases = new HashSet<>();

    public Map<CapacityType, Capacity> capacities = new HashMap<>();

}
