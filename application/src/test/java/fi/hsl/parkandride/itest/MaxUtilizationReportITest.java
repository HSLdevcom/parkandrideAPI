// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.itest;

import com.jayway.restassured.response.Response;
import fi.hsl.parkandride.core.domain.*;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static fi.hsl.parkandride.core.domain.CapacityType.BICYCLE_SECURE_SPACE;
import static fi.hsl.parkandride.core.domain.CapacityType.CAR;
import static fi.hsl.parkandride.core.domain.Usage.PARK_AND_RIDE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.joda.time.DateTimeConstants.*;

public class MaxUtilizationReportITest extends AbstractReportingITest {
    private static final String MAX_UTILIZATION = "MaxUtilization";

    // ---------------------
    // MAX UTILIZATION REPORT
    // ---------------------

    @Test
    public void report_MaxUtilization_asOperator() {
        // Record mock usage data
        addMockMaxUtilizations(facility1, apiUser);
        addMockMaxUtilizations(facility2, apiUser2);

        final Response whenPostingToReportUrl = postToReportUrl(baseParams(), MAX_UTILIZATION, operator1User);

        // If this succeeds, the response was a valid excel file
        withWorkbook(whenPostingToReportUrl, workbook -> {
            assertThat(getSheetNames(workbook)).containsExactly("Tiivistelmäraportti", "Selite");

            checkSheetContents(workbook, 0,
                    headers(),
                    hubRow(operator1, PARK_AND_RIDE, CAR, DayType.BUSINESS_DAY, 50, 100),
                    hubRow(operator1, PARK_AND_RIDE, CAR, DayType.SATURDAY, 50, 50),
                    hubRow(operator1, PARK_AND_RIDE, CAR, DayType.SUNDAY, 50, 20)
            );
        });
    }

    @Test
    public void report_MaxUtilization_asAdmin() {
        // Record mock usage data
        addMockMaxUtilizations(facility1, apiUser);
        addMockMaxUtilizations(facility2, apiUser2);

        final Response whenPostingToReportUrl = postToReportUrl(baseParams(), MAX_UTILIZATION, adminUser);

        checkSheetContents(whenPostingToReportUrl, 0,
                headers(),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.BUSINESS_DAY, 100, 100),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.SATURDAY, 100, 50),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.SUNDAY, 100, 20)
        );
    }

    private List<String> headers() {
        return asList("Alueen nimi",
                "Kunta",
                "Operaattori",
                "Käyttötapa",
                "Ajoneuvotyyppi",
                "Status",
                "Pysäköintipaikkojen määrä",
                "Päivätyyppi",
                "Keskimääräinen maksimitäyttöaste");
    }

    private void addMockMaxUtilizations(Facility f, User apiUser) {
        final Integer capacity = f.builtCapacity.get(CAR);
        // Day 15 to ensure that weekdays stay in the same month
        final DateTime baseDate = BASE_DATE.toDateTimeAtCurrentTime().withDayOfMonth(15);
        facilityService.registerUtilization(f.id, asList(
                utilize(CAR, capacity, baseDate.withDayOfWeek(MONDAY).minusWeeks(1), f),
                utilize(CAR, capacity, baseDate.withDayOfWeek(SATURDAY).minusWeeks(1), f),
                utilize(CAR, capacity, baseDate.withDayOfWeek(SUNDAY).minusWeeks(1), f),

                // 50/50 = 100%
                utilize(CAR, 0, baseDate.withDayOfWeek(MONDAY), f),
                // 25/50 =  50%
                utilize(CAR, capacity - (capacity / 2), baseDate.withDayOfWeek(SATURDAY), f),
                // 10/50 =  20%
                utilize(CAR, capacity - (capacity / 5), baseDate.withDayOfWeek(SUNDAY), f),

                // BICYCLE_SECURE_SPACE does not exist in built capacity, should not fail
                utilize(BICYCLE_SECURE_SPACE, 0, baseDate.withDayOfWeek(MONDAY), f)
        ), apiUser);
    }

    private List<String> hubRow(Operator operator, Usage usage, CapacityType type, DayType dayType, Integer totalCapacity, Integer percentage) {
        return hubRow(singletonList(operator), usage, type, dayType, totalCapacity, percentage);
    }

    private List<String> hubRow(List<Operator> operators, Usage usage, CapacityType type, DayType dayType, Integer totalCapacity, Integer percentage) {
        return asList(
                hub.name.fi,
                "Helsinki",
                operators.stream().map(o -> o.name.fi).collect(joining(", ")),
                translationService.translate(usage),
                translationService.translate(type),
                translationService.translate(facility1.status),
                totalCapacity.toString(),
                translationService.translate(dayType),
                String.format("%d%%", percentage)
        );
    }

}
