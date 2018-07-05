// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import fi.hsl.parkandride.core.domain.validation.PhoneOrEmailRequired;
import org.hibernate.validator.constraints.Email;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@PhoneOrEmailRequired
public class Contact implements OperatorEntity {

    public Long id;

    @NotNull
    @Valid
    public MultilingualString name;

    public Long operatorId;

    public Phone phone;

    @Email
    public String email;

    @Valid
    public Address address;

    @Valid
    public MultilingualString openingHours;

    @Valid
    public MultilingualString info;

    @Override
    public Long operatorId() {
        return operatorId;
    }
}
