// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import fi.hsl.parkandride.core.domain.validation.MinElement;
import fi.hsl.parkandride.core.domain.validation.NotNullElement;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class FacilityCapacityHistory {
    public Long facilityId;
    public DateTime startDate;
    public DateTime endDate;

    @NotNullElement
    @MinElement(1)
    @NotNull
    public Map<CapacityType, Integer> builtCapacity = newHashMap();

    public List<UnavailableCapacity> unavailableCapacities;

    public FacilityCapacityHistory() {
    }

    public FacilityCapacityHistory(Long facilityId, DateTime startDate, DateTime endDate, Map<CapacityType, Integer> builtCapacity) {
        this.facilityId = facilityId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.builtCapacity = builtCapacity;
    }

    public FacilityCapacityHistory(Long facilityId, DateTime startDate, DateTime endDate, Map<CapacityType, Integer> builtCapacity, List<UnavailableCapacity> unavailableCapacities) {
        this(facilityId, startDate, endDate, builtCapacity);
        this.unavailableCapacities = unavailableCapacities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FacilityCapacityHistory)) {
            return false;
        }
        FacilityCapacityHistory that = (FacilityCapacityHistory) o;
        return Objects.equal(facilityId, that.facilityId) &&
                Objects.equal(startDate, that.startDate) &&
                Objects.equal(endDate, that.endDate) &&
                Objects.equal(builtCapacity, that.builtCapacity) &&
                Objects.equal(unavailableCapacities, that.unavailableCapacities);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(facilityId, startDate, endDate, builtCapacity, unavailableCapacities);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("facilityId", facilityId)
                .add("startDate", startDate)
                .add("endDate", endDate)
                .add("builtCapacity", builtCapacity)
                .add("unavailableCapacities", unavailableCapacities)
                .toString();
    }


}
