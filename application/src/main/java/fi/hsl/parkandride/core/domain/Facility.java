// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import com.google.common.collect.Maps;
import fi.hsl.parkandride.core.domain.validation.ElementLength;
import fi.hsl.parkandride.core.domain.validation.NotBlankElement;
import fi.hsl.parkandride.core.domain.validation.NotNullElement;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.util.Collections.sort;
import static java.util.stream.Collectors.*;

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
    @ElementLength(min = 0, max = 255)
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

    public void normalize() {
        this.pricing = pricingMethod.getPricing(this);
    }
}
