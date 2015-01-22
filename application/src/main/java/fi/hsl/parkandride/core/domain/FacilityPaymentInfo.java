package fi.hsl.parkandride.core.domain;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class FacilityPaymentInfo {
    public boolean parkAndRideAuthRequired;

    @Valid
    public MultilingualString detail;

    @Valid
    public MultilingualUrl url;

    @NotNull
    public Set<PaymentMethod> paymentMethods = new HashSet<>();
}
