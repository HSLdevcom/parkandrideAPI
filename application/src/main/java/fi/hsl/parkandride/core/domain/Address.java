// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import com.google.common.base.MoreObjects;

public class Address {

    @Valid
    public MultilingualString streetAddress;

    @Pattern(regexp="\\d{5}")
    public String postalCode;

    @Valid
    public MultilingualString city;

    public Address() {}

    public Address(String streetAddress, String postalCode, String city) {
        this.streetAddress = streetAddress != null ? new MultilingualString(streetAddress, streetAddress, streetAddress) : null;
        this.postalCode = postalCode;
        this.city = city != null ? new MultilingualString(city, city, city) : null;
    }

    public Address(MultilingualString streetAddress, String postalCode, MultilingualString city) {
        this.streetAddress = streetAddress;
        this.postalCode = postalCode;
        this.city = city;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Address) {
            Address other = (Address) obj;
            return Objects.equals(this.streetAddress, other.streetAddress)
                    && Objects.equals(this.postalCode, other.postalCode)
                    && Objects.equals(this.city, other.city);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hashCode = (streetAddress == null ? 0 : streetAddress.hashCode());
        hashCode = 31*hashCode + (postalCode == null ? 0 : postalCode.hashCode());
        return 31*hashCode + (city == null ? 0 : city.hashCode());
    }

    public MultilingualString getStreetAddress() {
        return streetAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public MultilingualString getCity() {
        return city;
    }

    public String toString() {
        return MoreObjects.toStringHelper(Address.class)
                .add("streetAddress", streetAddress)
                .add("postalCode", postalCode)
                .add("city", city)
                .toString();
    }
}
