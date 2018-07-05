// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.joda.time.DateTime;
import org.junit.Test;

import static fi.hsl.parkandride.core.domain.FacilityStatus.INACTIVE;
import static org.assertj.core.api.Assertions.assertThat;

public class FacilityStatusHistoryTest {

    private static final DateTime START = new DateTime().minusMonths(1);
    private static final DateTime END = new DateTime();
    private static final MultilingualString STATUS = new MultilingualString("status");

    @Test
    public void simpleEquals() {
        final FacilityStatusHistory eka = new FacilityStatusHistory(123l, START, END, INACTIVE, STATUS);
        final FacilityStatusHistory toka = new FacilityStatusHistory();
        toka.facilityId = 123l;
        toka.startDate = START;
        toka.endDate = END;
        toka.status = INACTIVE;
        toka.statusDescription = STATUS;
        assertThat(eka).isEqualTo(toka);
    }

    @Test
    public void equalsHashCodeWork() {
        EqualsVerifier.forClass(FacilityStatusHistory.class)
                .allFieldsShouldBeUsed()
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

}