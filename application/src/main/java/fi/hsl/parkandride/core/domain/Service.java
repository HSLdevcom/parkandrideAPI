package fi.hsl.parkandride.core.domain;

import javax.validation.constraints.NotNull;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class Service {

    public Long id;

    @ApiModelProperty(required = true)
    @NotNull
    public MultilingualString name;

    public Long getId() {
        return id;
    }

    public MultilingualString getName() {
        return name;
    }
}
