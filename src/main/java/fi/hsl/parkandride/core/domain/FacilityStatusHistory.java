// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.joda.time.DateTime;

public class FacilityStatusHistory implements HasInterval {
    public Long facilityId;
    public DateTime startDate;
    public DateTime endDate;
    public FacilityStatus status;
    public MultilingualString statusDescription;

    public FacilityStatusHistory() {
    }

    public FacilityStatusHistory(Long facilityId, DateTime startDate, DateTime endDate, FacilityStatus status, MultilingualString statusDescription) {
        this.facilityId = facilityId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.statusDescription = statusDescription;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FacilityStatusHistory)) {
            return false;
        }
        FacilityStatusHistory that = (FacilityStatusHistory) o;
        return Objects.equal(facilityId, that.facilityId) &&
                Objects.equal(startDate, that.startDate) &&
                Objects.equal(endDate, that.endDate) &&
                Objects.equal(status, that.status) &&
                Objects.equal(statusDescription, that.statusDescription);
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(facilityId, startDate, endDate, status, statusDescription);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("facilityId", facilityId)
                .add("startDate", startDate)
                .add("endDate", endDate)
                .add("status", status)
                .add("statusDescription", statusDescription)
                .toString();
    }


    @Override
    public DateTime getStart() {
        return startDate;
    }

    @Override
    public DateTime getEnd() {
        return endDate;
    }
}
