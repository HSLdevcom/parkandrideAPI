package fi.hsl.parkandride.core.domain;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

public class FacilityPaymentInfo {
    public boolean parkAndRideAuthRequired;

    @Valid
    public MultilingualString info;

    @Valid
    public MultilingualString url;

    public Set<Long> paymentMethodIds = new HashSet<>();
}
