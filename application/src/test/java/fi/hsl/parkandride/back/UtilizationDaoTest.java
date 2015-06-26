// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import com.mysema.commons.lang.CloseableIterator;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.Utilization;
import fi.hsl.parkandride.core.domain.UtilizationKey;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Set;

import static fi.hsl.parkandride.core.domain.CapacityType.CAR;
import static fi.hsl.parkandride.core.domain.CapacityType.MOTORCYCLE;
import static fi.hsl.parkandride.core.domain.Usage.COMMERCIAL;
import static fi.hsl.parkandride.core.domain.Usage.HSL_TRAVEL_CARD;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class UtilizationDaoTest extends AbstractDaoTest {

    @Inject Dummies dummies;
    @Inject UtilizationRepository utilizationDao;

    private long facilityId;

    @Before
    public void initialize() {
        facilityId = dummies.createFacility();
    }


    // finding the latest utilization

    @Test
    public void findLatestUtilization_when_nothing_to_find() {
        Set<Utilization> results = utilizationDao.findLatestUtilization(facilityId);

        assertThat(results).isEmpty();
    }

    @Test
    public void findLatestUtilization_when_empty_list_registered() {
        Set<Utilization> results = utilizationDao.findLatestUtilization(facilityId);

        utilizationDao.insertUtilizations(emptyList());

        assertThat(results).isEmpty();
    }

    @Test
    public void findLatestUtilization_returns_latest_entry() {
        Utilization u1 = newUtilization(facilityId, new DateTime(2000, 1, 1, 12, 0), 100);
        Utilization u2 = newUtilization(facilityId, new DateTime(2000, 1, 1, 13, 0), 200);
        utilizationDao.insertUtilizations(asList(u1, u2));

        Set<Utilization> results = utilizationDao.findLatestUtilization(facilityId);

        assertThat(results).containsOnly(u2);
    }

    @Test
    public void findLatestUtilization_returns_each_capacity_type_and_usage_combination() {
        Utilization u1 = newUtilization(facilityId, new DateTime(2000, 1, 1, 12, 0), 100);
        u1.capacityType = CAR;
        u1.usage = HSL_TRAVEL_CARD;
        Utilization u2 = newUtilization(facilityId, new DateTime(2000, 1, 1, 13, 0), 200);
        u2.capacityType = CAR;
        u2.usage = COMMERCIAL;
        Utilization u3 = newUtilization(facilityId, new DateTime(2000, 1, 1, 14, 0), 300);
        u3.capacityType = MOTORCYCLE;
        u3.usage = HSL_TRAVEL_CARD;
        utilizationDao.insertUtilizations(asList(u1, u2, u3));

        Set<Utilization> results = utilizationDao.findLatestUtilization(facilityId);

        assertThat(results).containsOnly(u1, u2, u3);
    }


    // finding utilizations by date range

    @Test
    public void findUtilizationsBetween_limits_to_start_and_end_time_inclusive_ordered_by_time() {
        Utilization u1 = newUtilization(facilityId, new DateTime(2000, 1, 1, 12, 0, 0, 1), 100);
        Utilization u2 = newUtilization(facilityId, new DateTime(2000, 1, 1, 12, 0, 0, 2), 200);
        Utilization u3 = newUtilization(facilityId, new DateTime(2000, 1, 1, 12, 0, 0, 3), 300);
        Utilization u4 = newUtilization(facilityId, new DateTime(2000, 1, 1, 12, 0, 0, 4), 400);
        Utilization u5 = newUtilization(facilityId, new DateTime(2000, 1, 1, 12, 0, 0, 5), 500);
        UtilizationKey key = u1.getUtilizationKey();
        utilizationDao.insertUtilizations(asList(u1, u2, u3, u4, u5));

        try (CloseableIterator<Utilization> results = utilizationDao.findUtilizationsBetween(key, u2.timestamp, u4.timestamp)) {

            assertThat(results).containsExactly(u2, u3, u4);
        }
    }

    @Test
    public void findUtilizationsBetween_is_facility_specific() {
        DateTime time = new DateTime(2000, 1, 1, 12, 0);
        Utilization u1 = newUtilization(dummies.createFacility(), time, 100);
        Utilization u2 = newUtilization(dummies.createFacility(), time, 200);
        utilizationDao.insertUtilizations(asList(u1, u2));

        try (CloseableIterator<Utilization> results = utilizationDao.findUtilizationsBetween(u1.getUtilizationKey(), time, time)) {

            assertThat(results).containsExactly(u1);
        }
    }

    @Test
    public void findUtilizationsBetween_is_capacity_type_specific() {
        DateTime time = new DateTime(2000, 1, 1, 12, 0);
        Utilization u1 = newUtilization(facilityId, time, 100);
        u1.capacityType = CAR;
        Utilization u2 = newUtilization(facilityId, time, 200);
        u2.capacityType = MOTORCYCLE;
        utilizationDao.insertUtilizations(asList(u1, u2));

        try (CloseableIterator<Utilization> results = utilizationDao.findUtilizationsBetween(u1.getUtilizationKey(), time, time)) {

            assertThat(results).containsExactly(u1);
        }
    }

    @Test
    public void findUtilizationsBetween_is_usage_specific() {
        DateTime time = new DateTime(2000, 1, 1, 12, 0);
        Utilization u1 = newUtilization(facilityId, time, 100);
        u1.usage = COMMERCIAL;
        Utilization u2 = newUtilization(facilityId, time, 200);
        u2.usage = HSL_TRAVEL_CARD;
        utilizationDao.insertUtilizations(asList(u1, u2));

        try (CloseableIterator<Utilization> results = utilizationDao.findUtilizationsBetween(u1.getUtilizationKey(), time, time)) {

            assertThat(results).containsExactly(u1);
        }
    }


    // helpers

    private static Utilization newUtilization(long facilityId, DateTime time, int spacesAvailable) {
        Utilization u = new Utilization();
        u.facilityId = facilityId;
        u.capacityType = CAR;
        u.usage = HSL_TRAVEL_CARD;
        u.timestamp = time;
        u.spacesAvailable = spacesAvailable;
        return u;
    }
}
