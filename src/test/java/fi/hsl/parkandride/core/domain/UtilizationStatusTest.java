// Copyright © 2017 HSL <https://www.hsl.fi>
// utilization program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import static fi.hsl.parkandride.core.domain.CapacityType.CAR;
import static fi.hsl.parkandride.core.domain.Usage.PARK_AND_RIDE;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

public class UtilizationStatusTest {

    @Test
    public void instantiation_equals() {
        DateTime now = new DateTime();
        Utilization utilization = new Utilization();
        utilization.facilityId = 123L;
        utilization.capacityType = CAR;
        utilization.usage = PARK_AND_RIDE;
        utilization.timestamp = now;
        utilization.spacesAvailable = 456;
        utilization.capacity = 789;
        Facility facility = mock(Facility.class);
        when(facility.isOpen(CAR, PARK_AND_RIDE, now))
                .thenReturn(true)
                .thenReturn(false);

        UtilizationStatus utilizationStatus = new UtilizationStatus(utilization, facility, now);

        assertThat(utilizationStatus).isEqualTo(new UtilizationStatus(utilization, true));
        assertThat(utilizationStatus.facilityId).isEqualTo(utilization.facilityId);
        assertThat(utilizationStatus.capacityType).isEqualTo(utilization.capacityType);
        assertThat(utilizationStatus.usage).isEqualTo(utilization.usage);
        assertThat(utilizationStatus.timestamp).isEqualTo(utilization.timestamp);
        assertThat(utilizationStatus.spacesAvailable).isEqualTo(utilization.spacesAvailable);
        assertThat(utilizationStatus.capacity).isEqualTo(utilization.capacity);
        assertThat(new UtilizationStatus(utilization, facility, now).openNow).isEqualTo(false);
    }

}
