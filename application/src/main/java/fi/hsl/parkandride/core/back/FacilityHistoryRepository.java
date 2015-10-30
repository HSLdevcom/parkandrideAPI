// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.Map;

public interface FacilityHistoryRepository {

    /**
     * Updates the capacity history for the given facility. Ends the previous history entry to given date if applicable.
     * @param currentDate the end date for the previous entry and the start date for the entry to insert
     * @param facilityId
     * @param builtCapacity
     * @param unavailableCapacities
     */
    void updateCapacityHistory(DateTime currentDate, long facilityId, Map<CapacityType, Integer> builtCapacity, List<UnavailableCapacity> unavailableCapacities);

    /**
     * Updates the status history for the given facility. Ends the previous history entry to given date if applicable.
     * @param currentDate the end date for the previous entry and the start date for the entry to insert
     * @param facilityId
     */
    void updateStatusHistory(DateTime currentDate, long facilityId, FacilityStatus newStatus, MultilingualString statusDescription);

    /**
     * Get the whole capacity history for the facility ordered by start date asc
     */
    List<FacilityCapacityHistory> getCapacityHistory(long facilityId);


    /**
     * Get the capacity history for the facility between the dates ordered by start date asc
     */
    List<FacilityCapacityHistory> getCapacityHistory(long facilityId, LocalDate startInclusive, LocalDate endInclusive);

    /**
     * Get the whole status history for the facility ordered by start date asc
     */
    List<FacilityStatusHistory> getStatusHistory(long facilityId);

    /**
     * Get the status history for the facility between the dates ordered by start date asc
     */
    List<FacilityStatusHistory> getStatusHistory(long facilityId, LocalDate startInclusive, LocalDate endInclusive);
}
