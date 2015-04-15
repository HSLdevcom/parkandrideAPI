// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import fi.hsl.parkandride.core.domain.validation.NotNullElement;

public class FacilityPaymentInfo {

    @Valid
    public MultilingualString detail;

    @Valid
    public MultilingualUrl url;

    @NotNull
    @NotNullElement
    public NullSafeSortedSet<PaymentMethod> paymentMethods = new NullSafeSortedSet<>();

}
