package fi.hsl.parkandride.core.domain;

import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.geolatte.geom.Geometry;
import org.hibernate.validator.constraints.NotBlank;

import com.google.common.collect.Sets;

public class Facility {

    public Long id;

    @NotBlank
    public String name;

    @NotNull
    public Geometry border;

    public Set<String> aliases = new HashSet<>();

    @Valid
    public Map<CapacityType, Capacity> capacities = new HashMap<>();

}
