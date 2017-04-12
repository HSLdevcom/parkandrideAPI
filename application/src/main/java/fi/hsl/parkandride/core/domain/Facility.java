// Copyright © 2016 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.hsl.parkandride.core.domain.validation.ElementLength;
import fi.hsl.parkandride.core.domain.validation.NotBlankElement;
import fi.hsl.parkandride.core.domain.validation.NotNullElement;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static fi.hsl.parkandride.core.domain.FacilityStatus.EXCEPTIONAL_SITUATION;
import static fi.hsl.parkandride.core.domain.FacilityStatus.IN_OPERATION;
import static java.util.Collections.sort;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

public class Facility extends FacilityInfo {

    private static final DateTimeZone TIME_ZONE = DateTimeZone.forID("Europe/Helsinki");

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
        openingHours.initialize(pricing, new DateTime(), status);
    }

    public Set<Usage> analyzeUsages() {
        return pricing.stream().collect(mapping(Pricing::getUsage, toSet()));
    }

    public void normalize() {
        this.pricing = pricingMethod.getPricing(this);
    }

    public boolean isOpen(CapacityType capacityType, Usage usage, DateTime now) {
        if (status == IN_OPERATION || status == EXCEPTIONAL_SITUATION) {
            now = now.withZone(getTimeZone());
            DayType dayType = DayType.valueOf(now);
            LocalTime currentTime = now.toLocalTime();
            return pricing.stream()
                    .filter(pricing ->
                            pricing.capacityType.equals(capacityType)
                            && pricing.usage.equals(usage)
                            && pricing.dayType.equals(dayType))
                    .anyMatch(pricing -> pricing.time.includes(currentTime));
        } else {
            return false;
        }
    }

    @JsonIgnore
    public DateTimeZone getTimeZone() {
        return TIME_ZONE;
    }
}
