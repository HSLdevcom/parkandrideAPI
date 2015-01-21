package fi.hsl.parkandride.core.domain;

import java.util.Map;

import javax.validation.Valid;

public class OpeningHours {

    public Map<DayType, TimeDuration> byDayType;

    @Valid
    public MultilingualString info;

    @Valid
    public MultilingualUrl url;

}
