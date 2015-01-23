package fi.hsl.parkandride.core.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;

import com.wordnik.swagger.annotations.ApiModelProperty;

import fi.hsl.parkandride.core.domain.validation.PhoneOrEmailRequired;

@PhoneOrEmailRequired
public class Contact implements OperatorEntity {

    public Long id;

    @ApiModelProperty(required = true)
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

    public Long getId() {
        return id;
    }

    public MultilingualString getName() {
        return name;
    }

    public Phone getPhone() {
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

    @Override
    public Long operatorId() {
        return operatorId;
    }
}
