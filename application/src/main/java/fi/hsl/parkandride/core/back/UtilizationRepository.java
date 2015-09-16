// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.back;

import com.mysema.commons.lang.CloseableIterator;
import fi.hsl.parkandride.core.domain.Utilization;
import fi.hsl.parkandride.core.domain.UtilizationKey;
import fi.hsl.parkandride.core.domain.UtilizationSearch;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UtilizationRepository {

    void insertUtilizations(List<Utilization> utilizations);

    Set<Utilization> findLatestUtilization(long facilityId);

    Optional<Utilization> findUtilizationAtInstant(UtilizationKey utilizationKey, DateTime instant);

    CloseableIterator<Utilization> findUtilizationsBetween(UtilizationKey utilizationKey, DateTime start, DateTime end);

    List<Utilization> findUtilizationsWithResolution(UtilizationKey utilizationKey, DateTime start, DateTime end, Minutes resolution);

    CloseableIterator<Utilization> findUtilizations(UtilizationSearch search);
}
