// Copyright © 2017 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import com.google.common.base.MoreObjects;
import org.joda.time.DateTime;

import java.util.Objects;

public class UtilizationStatus extends Utilization {

    public boolean openNow;

    public UtilizationStatus() {}

    public UtilizationStatus(Utilization utilization, Facility facility, DateTime now) {
        this(utilization, facility.isOpen(utilization.capacityType, utilization.usage, now));
    }

    public UtilizationStatus(Utilization utilization, boolean openNow) {
        this.facilityId = utilization.facilityId;
        this.capacityType = utilization.capacityType;
        this.usage = utilization.usage;
        this.timestamp = utilization.timestamp;
        this.spacesAvailable = utilization. spacesAvailable;
        this.capacity = utilization.capacity;
        this.openNow = openNow;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj.getClass().equals(UtilizationStatus.class) )) {
            return false;
        }
        UtilizationStatus that = (UtilizationStatus) obj;
        return Objects.equals(this.facilityId, that.facilityId)
                && Objects.equals(this.capacityType, that.capacityType)
                && Objects.equals(this.usage, that.usage)
                && Objects.equals(this.timestamp, that.timestamp)
                && Objects.equals(this.spacesAvailable, that.spacesAvailable)
                && Objects.equals(this.capacity, that.capacity)
                && this.openNow == that.openNow;
    }

    @Override
    public int hashCode() {
        return Objects.hash(facilityId, capacityType, usage, timestamp, spacesAvailable, capacity, openNow);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("facilityId", facilityId)
                .add("capacityType", capacityType)
                .add("usage", usage)
                .add("timestamp", timestamp)
                .add("spacesAvailable", spacesAvailable)
                .add("capacity", capacity)
                .add("openNow", openNow)
                .toString();
    }

}
