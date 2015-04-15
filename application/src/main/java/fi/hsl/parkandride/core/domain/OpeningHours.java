// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

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
