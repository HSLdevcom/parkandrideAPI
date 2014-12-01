package fi.hsl.parkandride.core.domain;

import java.util.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.geolatte.geom.Geometry;

public class Facility {

    public Long id;

    @NotNull
    @Valid
    public MultilingualString name;

    @NotNull
    public Geometry location;

    @ElementNotBlank
    @ElementLength(min=0, max=255)
    public Set<String> aliases = new HashSet<>();

    @Valid
    public Map<CapacityType, Capacity> capacities = new HashMap<>();

    @Valid
    public List<Port> ports = new ArrayList<>();

    public Set<Long> serviceIds = new HashSet<>();

    public Map<ContactType, Long> contacts = new HashMap<>();


    public Long getId() {
        return id;
    }

    public MultilingualString getName() {
        return name;
    }

    public Geometry getLocation() {
        return location;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    public Map<CapacityType, Capacity> getCapacities() {
        return capacities;
    }

    public List<Port> getPorts() {
        return ports;
    }

    public Set<Long> getServiceIds() {
        return serviceIds;
    }

    public Map<ContactType, Long> getContacts() {
        return contacts;
    }
}
