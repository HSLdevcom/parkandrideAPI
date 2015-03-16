package fi.hsl.parkandride.core.domain;

import java.util.Map;

import javax.validation.Valid;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class OpeningHours {

    /**
     * Summary of min( pricing.from ) and max( pricing.until )
     */
    @ApiModelProperty(required = false, value = "Read-only summary of pricing rows' opening hours")
    public Map<DayType, TimeDuration> byDayType;

    @ApiModelProperty(required = false)
    @Valid
    public MultilingualString info;

    @ApiModelProperty(required = false)
    @Valid
    public MultilingualUrl url;

}
