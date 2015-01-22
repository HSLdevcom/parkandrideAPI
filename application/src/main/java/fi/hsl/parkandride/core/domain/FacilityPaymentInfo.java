package fi.hsl.parkandride.core.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class FacilityPaymentInfo {
    public boolean parkAndRideAuthRequired;

    @Valid
    public MultilingualString detail;

    @Valid
    public MultilingualUrl url;

    @NotNull
    @ElementNotNull
    public NullSafeSortedSet<PaymentMethod> paymentMethods = new NullSafeSortedSet<>();

}
