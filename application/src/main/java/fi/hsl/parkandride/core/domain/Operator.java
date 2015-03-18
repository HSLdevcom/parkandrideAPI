// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class Operator implements OperatorEntity {

    public Long id;

    @ApiModelProperty(required = true)
    public MultilingualString name;

    public Operator() {}

    public Operator(String name) {
        this.name = new MultilingualString(name);
    }

    @Override
    public Long operatorId() {
        return id;
    }

}
