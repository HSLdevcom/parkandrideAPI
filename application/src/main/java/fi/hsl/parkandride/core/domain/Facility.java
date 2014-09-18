package fi.hsl.parkandride.core.domain;

import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.geolatte.geom.Geometry;

import com.google.common.collect.Sets;

public class Facility {

    public Long id;

    public String name;

    public Geometry border;

    public SortedSet<String> aliases = new TreeSet<>();

    public Map<CapacityType, Capacity> capacities = new HashMap<>();

}
