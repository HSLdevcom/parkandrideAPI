// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class FacilityContacts {

    @NotNull
    public Long emergency;

    @NotNull
    public Long operator;

    public Long service;

    public FacilityContacts() {
    }

    public FacilityContacts(Long emergency, Long operator) {
        this(emergency, operator, null);
    }

    public FacilityContacts(Long emergency, Long operator, Long service) {
        this.emergency = emergency;
        this.operator = operator;
        this.service = service;
    }

    @Override
    public int hashCode() {
        int hashCode = emergency == null ? 1 : emergency.hashCode();
        hashCode = 31 * hashCode + (operator == null ? 0 : operator.hashCode());
        return 31 * hashCode + (service == null ? 0 : service.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof FacilityContacts) {
            FacilityContacts other = (FacilityContacts) obj;
            return Objects.equals(this.emergency, other.emergency)
                    && Objects.equals(this.operator, other.operator)
                    && Objects.equals(this.service, other.service);
        } else {
            return false;
        }
    }

}
