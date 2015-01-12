package fi.hsl.parkandride.core.domain;

import java.util.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.Polygon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class Facility implements OperatorEntity {

    public Long id;

    @ApiModelProperty(required = true)
    @NotNull
    @Valid
    public MultilingualString name;

    @ApiModelProperty(required = true)
    @NotNull
    public Polygon location;

    @ApiModelProperty(required = true)
    @NotNull
    public Long operatorId;

    public Map<CapacityType, Integer> builtCapacity = Maps.newHashMap();

    @Valid
    public SortedSet<Pricing> pricing = Sets.newTreeSet();

    @ElementNotBlank
    @ElementLength(min=0, max=255)
    public Set<String> aliases = new HashSet<>();

    @Valid
    public List<Port> ports = new ArrayList<>();

    public Set<Long> serviceIds = new HashSet<>();

    @ApiModelProperty(required = true)
    @NotNull
    @Valid
    public FacilityContacts contacts = new FacilityContacts();

    public FacilityPaymentInfo paymentInfo = new FacilityPaymentInfo();


    public Long getId() {
        return id;
    }

    public MultilingualString getName() {
        return name;
    }

    public Polygon getLocation() {
        return location;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    public List<Port> getPorts() {
        return ports;
    }

    public Set<Long> getServiceIds() {
        return serviceIds;
    }

    public FacilityContacts getContacts() {
        return contacts;
    }

    public Long operatorId() {
        return operatorId;
    }

}
