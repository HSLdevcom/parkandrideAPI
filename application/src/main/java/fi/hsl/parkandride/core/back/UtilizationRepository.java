// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.Utilization;
import fi.hsl.parkandride.core.domain.UtilizationKey;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Set;

public interface UtilizationRepository {

    void insertUtilizations(List<Utilization> utilizations);

    Set<Utilization> findLatestUtilization(long facilityId);

    List<Utilization> findUtilizationsBetween(UtilizationKey utilizationKey, DateTime start, DateTime end);
}
