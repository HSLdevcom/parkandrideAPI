// Copyright © 2016 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import com.google.common.collect.Maps;
import org.joda.time.DateTime;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static fi.hsl.parkandride.core.domain.FacilityStatus.EXCEPTIONAL_SITUATION;
import static fi.hsl.parkandride.core.domain.FacilityStatus.IN_OPERATION;
import static java.util.stream.Collectors.*;

public class OpeningHours {

    public Boolean openNow;

    /**
     * Summary of min( pricing.from ) and max( pricing.until )
     */
    public Map<DayType, TimeDuration> byDayType;

    @Valid
    public MultilingualString info;

    @Valid
    public MultilingualUrl url;

    public void initialize(List<Pricing> pricing, DateTime now, FacilityStatus status) {
        byDayType = Maps.newLinkedHashMap();
        // Opening hours by day type: min(time.from) and max(time.until) of pricing rows by dayType
        pricing.stream()
                .collect(groupingBy(Pricing::getDayType, mapping(Pricing::getTime, reducing(TimeDuration::add))))
                .forEach((dayType, time) -> byDayType.put(dayType, time.get()));

        TimeDuration openToday = byDayType.get(DayType.valueOf(now));
        openNow = (status == IN_OPERATION || status == EXCEPTIONAL_SITUATION)
                && openToday != null
                && openToday.includes(now.toLocalTime());
    }
}
