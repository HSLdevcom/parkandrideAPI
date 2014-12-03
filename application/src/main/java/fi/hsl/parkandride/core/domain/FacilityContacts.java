package fi.hsl.parkandride.core.domain;

import java.util.Objects;

import javax.validation.constraints.NotNull;

public class FacilityContacts {

    @NotNull
    public Long emergency;

    @NotNull
    public Long operator;

    public Long service;

    public FacilityContacts() {}

    public FacilityContacts(Long emergency, Long operator) {
        this(emergency, operator, null);
    }

    public FacilityContacts(Long emergency, Long operator, Long service) {
        this.emergency = emergency;
        this.operator = operator;
        this.service = service;
    }

    public int hashCode() {
        int hashCode = emergency==null ? 1 : emergency.hashCode();
        hashCode = 31*hashCode + (operator==null ? 0 : operator.hashCode());
        return 31*hashCode + (service==null ? 0 : service.hashCode());
    }

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

    public Long getEmergency() {
        return emergency;
    }

    public Long getOperator() {
        return operator;
    }

    public Long getService() {
        return service;
    }
}
