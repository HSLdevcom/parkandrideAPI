package fi.hsl.parkandride.core.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;

@PhoneOrEmailRequired
public class Contact {

    public Long id;

    @NotNull
    @Valid
    public MultilingualString name;

    // TODO: @Phone
    public String phone;

    @Email
    public String email;

    @Valid
    public Address address;

    @Valid
    public MultilingualString openingHours;

    @Valid
    public MultilingualString info;

    public Long getId() {
        return id;
    }

    public MultilingualString getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public MultilingualString getOpeningHours() {
        return openingHours;
    }

    public MultilingualString getInfo() {
        return info;
    }

}
