package fi.hsl.parkandride.core.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.geolatte.geom.Geometry;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class Facility {

    public Long id;

    @NotNull
    @Valid
    public MultilingualString name;

    @NotNull
    public Geometry border;

    @ElementNotBlank
    @ElementLength(min=0, max=255)
    public Set<String> aliases = new HashSet<>();

    @Valid
    public Map<CapacityType, Capacity> capacities = new HashMap<>();


    public Long getId() {
        return id;
    }

    public MultilingualString getName() {
        return name;
    }

    public Geometry getBorder() {
        return border;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    public Map<CapacityType, Capacity> getCapacities() {
        return capacities;
    }

}
