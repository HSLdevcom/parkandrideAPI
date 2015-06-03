// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import javax.validation.Valid;
import java.util.Map;

public class OpeningHours {

    /**
     * Summary of min( pricing.from ) and max( pricing.until )
     */
    public Map<DayType, TimeDuration> byDayType;

    @Valid
    public MultilingualString info;

    @Valid
    public MultilingualUrl url;

}
