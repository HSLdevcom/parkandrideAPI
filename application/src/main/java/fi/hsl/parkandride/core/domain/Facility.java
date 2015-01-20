package fi.hsl.parkandride.core.domain;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.geolatte.geom.Polygon;

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

    public Map<CapacityType, Integer> builtCapacity = newHashMap();

    @Valid
    public List<Pricing> pricing = newArrayList();

    @Valid
    public List<UnavailableCapacity> unavailableCapacities = newArrayList();

    @ElementNotBlank
    @ElementLength(min=0, max=255)
    public Set<String> aliases = newHashSet();

    @Valid
    public List<Port> ports = newArrayList();

    public Set<Long> serviceIds = newHashSet();

    @ApiModelProperty(required = true)
    @NotNull
    @Valid
    public FacilityContacts contacts = new FacilityContacts();

    @Valid
    public FacilityPaymentInfo paymentInfo = new FacilityPaymentInfo();

    @Valid
    public OpeningHours openingHours = new OpeningHours();


    public Long operatorId() {
        return operatorId;
    }

}
