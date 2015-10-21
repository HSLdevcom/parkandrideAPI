// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fi.hsl.parkandride.core.domain.*;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static fi.hsl.parkandride.core.domain.CapacityType.CAR;
import static fi.hsl.parkandride.core.domain.CapacityType.ELECTRIC_CAR;
import static fi.hsl.parkandride.core.domain.FacilityStatus.EXCEPTIONAL_SITUATION;
import static fi.hsl.parkandride.core.domain.FacilityStatus.INACTIVE;
import static fi.hsl.parkandride.core.domain.Usage.PARK_AND_RIDE;
import static fi.hsl.parkandride.test.DateTimeTestUtils.withDate;
import static org.assertj.core.api.Assertions.assertThat;

public class FacilityHistoryDaoTest extends AbstractDaoTest {

    @Inject
    FacilityHistoryDao facilityHistoryDao;

    @Inject
    FacilityDao facilityDao;

    @Inject
    Dummies dummies;

    private Facility facility;

    final DateTime firstDate  = new DateTime().minusDays(10);
    final DateTime secondDate = firstDate.plusDays(1);
    final DateTime thirdDate  = firstDate.plusDays(2);
    final DateTime fourthDate = firstDate.plusDays(3);

    @Before
    public void initialize() {
        final Long dummyOperator = dummies.createDummyOperator();
        facility = FacilityDaoTest.createFacility(dummyOperator, new FacilityContacts(dummies.createDummyContact(), dummies.createDummyContact()));
    }

    @Test
    public void facility_status_history_is_saved() {
        // First date
        final long facilityId = withDate(firstDate, () -> {
            long facId = facilityDao.insertFacility(facility);
            assertThat(facilityHistoryDao.getStatusHistory(facId)).hasSize(1);
            return facId;
        });
        final Facility fac = facilityDao.getFacility(facilityId);

        // Second date
        withDate(secondDate, () -> {
            fac.status = FacilityStatus.INACTIVE;
            facilityDao.updateFacility(facilityId, fac);
            assertThat(facilityHistoryDao.getStatusHistory(facilityId)).hasSize(2);
        });

        // Third date
        // This shouldn't create more entries since state did not change
        withDate(thirdDate, () -> {
            facilityDao.updateFacility(facilityId, fac);
            assertThat(facilityHistoryDao.getStatusHistory(facilityId)).hasSize(2);
        });

        // Fourth date
        // But this should since the description changed
        final MultilingualString newStatus = new MultilingualString(FacilityDaoTest.STATUS_DESCRIPTION.fi);
        newStatus.sv = "Inte i bruk";
        withDate(fourthDate, () -> {
            fac.statusDescription = newStatus;
            facilityDao.updateFacility(facilityId, fac);
        });

        final FacilityStatusHistory first = new FacilityStatusHistory(facilityId, firstDate, secondDate, EXCEPTIONAL_SITUATION, FacilityDaoTest.STATUS_DESCRIPTION);
        final FacilityStatusHistory second = new FacilityStatusHistory(facilityId, secondDate, fourthDate, INACTIVE, FacilityDaoTest.STATUS_DESCRIPTION);
        final FacilityStatusHistory third = new FacilityStatusHistory(facilityId, fourthDate, null, INACTIVE, newStatus);

        // Get all of history
        final List<FacilityStatusHistory> history = facilityHistoryDao.getStatusHistory(facilityId);
        assertThat(history).containsExactly(first, second, third);

        // All included
        final List<FacilityStatusHistory> historyBetween = facilityHistoryDao.getStatusHistory(facilityId, firstDate.toLocalDate(), fourthDate.toLocalDate());
        assertThat(historyBetween).containsExactly(first, second, third);

        // Only second overlaps
        final List<FacilityStatusHistory> historyAt = facilityHistoryDao.getStatusHistory(facilityId, thirdDate.toLocalDate(), thirdDate.toLocalDate());
        assertThat(historyAt).containsExactly(second);
    }

    @Test
    public void facility_capacity_history_is_saved() {
        // First date
        final long facilityId = withDate(firstDate, () -> {
            facility.unavailableCapacities = newArrayList(new UnavailableCapacity(
                    CAR, PARK_AND_RIDE, 1
            ));
            long facId = facilityDao.insertFacility(facility);
            assertThat(facilityHistoryDao.getCapacityHistory(facId)).hasSize(1);
            return facId;
        });
        final Facility fac = facilityDao.getFacility(facilityId);
        final Map<CapacityType, Integer> original = ImmutableMap.copyOf(fac.builtCapacity);
        final List<UnavailableCapacity> unavailable = ImmutableList.copyOf(fac.unavailableCapacities);

        // Second date
        // We change a value
        final Map<CapacityType, Integer> modifiedCapacity = withDate(secondDate, () -> {
            fac.builtCapacity.put(CAR, 49);
            facilityDao.updateFacility(facilityId, fac);
            assertThat(facilityHistoryDao.getCapacityHistory(facilityId)).hasSize(2);
            return ImmutableMap.copyOf(fac.builtCapacity);
        });

        // Third date
        // Nothing saved, since nothing changes
        withDate(thirdDate, () -> {
            fac.status = INACTIVE;
            facilityDao.updateFacility(facilityId, fac);
            assertThat(facilityHistoryDao.getCapacityHistory(facilityId)).hasSize(2);
        });

        // Fourth date
        // We add an entry to unavailable capacities
        final List<UnavailableCapacity> modified = withDate(fourthDate, () -> {
            fac.unavailableCapacities.add(new UnavailableCapacity(
                    ELECTRIC_CAR, PARK_AND_RIDE, 5
            ));
            facilityDao.updateFacility(facilityId, fac);
            return ImmutableList.copyOf(fac.unavailableCapacities);
        });

        final FacilityCapacityHistory first = new FacilityCapacityHistory(facilityId, firstDate, secondDate, original, unavailable);
        final FacilityCapacityHistory second = new FacilityCapacityHistory(facilityId, secondDate, fourthDate, modifiedCapacity, unavailable);
        final FacilityCapacityHistory third = new FacilityCapacityHistory(facilityId, fourthDate, null, modifiedCapacity, modified);

        // Get all of history
        final List<FacilityCapacityHistory> history = facilityHistoryDao.getCapacityHistory(facilityId);
        assertThat(history).containsExactly(first, second, third);

        // All included
        final List<FacilityCapacityHistory> historyBetween = facilityHistoryDao.getCapacityHistory(facilityId, firstDate.toLocalDate(), fourthDate.toLocalDate());
        assertThat(historyBetween).containsExactly(first, second, third);

        // Only second overlaps
        final List<FacilityCapacityHistory> historyAt = facilityHistoryDao.getCapacityHistory(facilityId, thirdDate.toLocalDate(), thirdDate.toLocalDate());
        assertThat(historyAt).containsExactly(second);
    }

}