package fi.hsl.parkandride.core.domain;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static com.google.common.collect.Sets.newTreeSet;
import static java.util.Collections.sort;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.geolatte.geom.Polygon;

import com.google.common.collect.Maps;
import com.wordnik.swagger.annotations.ApiModelProperty;

import fi.hsl.parkandride.core.domain.validation.ElementLength;
import fi.hsl.parkandride.core.domain.validation.NotBlankElement;
import fi.hsl.parkandride.core.domain.validation.NotNullElement;

public class Facility extends FacilityInfo {

    @NotNull
    @NotNullElement
    @Valid
    public List<Pricing> pricing = newArrayList();

    @NotNull
    @NotNullElement
    @Valid
    public List<UnavailableCapacity> unavailableCapacities = newArrayList();

    @NotNull
    @NotBlankElement
    @ElementLength(min=0, max=255)
    public Set<String> aliases = newLinkedHashSet();

    @NotNull
    @NotNullElement
    @Valid
    public List<Port> ports = newArrayList();

    @NotNull
    @NotNullElement
    public NullSafeSortedSet<Service> services = new NullSafeSortedSet<>();

    @NotNull
    @Valid
    @ApiModelProperty(required = true)
    public FacilityContacts contacts = new FacilityContacts();

    @NotNull
    @Valid
    public FacilityPaymentInfo paymentInfo = new FacilityPaymentInfo();

    @NotNull
    @Valid
    public OpeningHours openingHours = new OpeningHours();


    /**
     * Expects a valid facility
     */
    public void initialize() {
        sort(pricing, Pricing.COMPARATOR);
        sort(unavailableCapacities, UnavailableCapacity.COMPARATOR);
        openingHours.byDayType = Maps.newLinkedHashMap();
        // Opening hours by day type: min(time.from) and max(time.until) of pricing rows by dayType
        pricing.stream().collect(groupingBy(Pricing::getDayType,
                mapping(Pricing::getTime, reducing(TimeDuration::add))))
                .forEach((dayType, time) -> openingHours.byDayType.put(dayType, time.get()));
    }

    public Set<Usage> analyzeUsages() {
        return pricing.stream().collect(mapping(Pricing::getUsage, toSet()));
    }

}
