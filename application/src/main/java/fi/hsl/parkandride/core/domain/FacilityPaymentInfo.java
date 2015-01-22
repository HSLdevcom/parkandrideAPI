package fi.hsl.parkandride.core.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class FacilityPaymentInfo {
    public boolean parkAndRideAuthRequired;

    @Valid
    public MultilingualString detail;

    @Valid
    public MultilingualUrl url;

    @NotNull
    public SortedSet<PaymentMethod> paymentMethods = new TreeSet<>();

}
