// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.geolatte.geom.Polygon;

import com.wordnik.swagger.annotations.ApiModelProperty;

import fi.hsl.parkandride.core.domain.validation.Coordinates;
import fi.hsl.parkandride.core.domain.validation.MinElement;
import fi.hsl.parkandride.core.domain.validation.NotNullElement;

public class FacilityInfo implements OperatorEntity {

    public Long id;

    @ApiModelProperty(required = true)
    @NotNull
    @Valid
    public MultilingualString name;

    @ApiModelProperty(required = true)
    @NotNull
    @Coordinates
    public Polygon location;

    @ApiModelProperty(required = true, value="Operator ID")
    @NotNull
    public Long operatorId;

    @ApiModelProperty(required = true)
    @NotNull
    public FacilityStatus status;

    @ApiModelProperty(required = true)
    @NotNull
    public PricingMethod pricingMethod;

    @ApiModelProperty(required = true)
    @Valid
    public MultilingualString statusDescription;

    @ApiModelProperty(required = true, value = "Built capacity by CapacityType, may be split or shared by different Usage types as defined by pricing")
    @NotNullElement
    @MinElement(1)
    @NotNull
    public Map<CapacityType, Integer> builtCapacity = newHashMap();

    /**
     * Summary of unique( pricing[*].usage )
     */
    @ApiModelProperty(required = false, value = "Read-only summary of distinct pricing rows' usages")
    public NullSafeSortedSet<Usage> usages = new NullSafeSortedSet<>();

    @Override
    public Long operatorId() {
        return operatorId;
    }

}
