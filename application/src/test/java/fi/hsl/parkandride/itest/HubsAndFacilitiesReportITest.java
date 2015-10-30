// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.itest;

import com.jayway.restassured.response.Response;
import fi.hsl.parkandride.core.domain.Operator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.google.common.collect.Lists.newArrayList;
import static fi.hsl.parkandride.core.domain.CapacityType.*;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.assertj.core.api.Assertions.assertThat;

public class HubsAndFacilitiesReportITest extends AbstractReportingITest {
    private static final String HUBS_AND_FACILITIES = "HubsAndFacilities";

    // ---------------------
    // HUBS AND FACILITIES REPORT
    // ---------------------

    @Test
    public void report_HubsAndFacilities_asOperator() {
        final Response whenPostingToReportUrl = postToReportUrl(baseParams(), HUBS_AND_FACILITIES, operator1User);

        // If this succeeds, the response was a valid excel file
        withWorkbook(whenPostingToReportUrl, workbook -> {

            assertThat(workbook.getNumberOfSheets()).isEqualTo(3);
            assertThat(getSheetNames(workbook)).containsExactly("Alueet", "Pysäköintipaikat", "Selite");

            // Check that only operator1 is displayed
            checkHubsAndFacilities_operatorsAre(workbook, operator1);

            // Check that hub info is displayed correctly
            checkHubsAndFacilities_hubInfo(workbook);
            // Check that facility info is displayed correctly
            checkHubsAndFacilities_facilityInfo(workbook);
        });
    }

    @Test
    public void report_HubsAndFacilities_asAdmin() {
        final Response whenPostingToReportUrl = postToReportUrl(baseParams(), HUBS_AND_FACILITIES, adminUser);

        // If this succeeds, the response was a valid excel file
        withWorkbook(whenPostingToReportUrl, workbook -> {
            checkHubsAndFacilities_operatorsAre(workbook, operator1, operator2);
        });

    }

    private void checkHubsAndFacilities_operatorsAre(Workbook workbook, Operator... operators) {
        final Sheet facilities = workbook.getSheetAt(1);
        final List<String> expectedColumns = newArrayList("Operaattori");
        expectedColumns.addAll(Arrays.stream(operators).map(o -> o.name.fi).collect(toList()));
        assertThat(getDataFromColumn(facilities, 3)).containsExactlyElementsOf(expectedColumns);
    }

    private void checkHubsAndFacilities_facilityInfo(Workbook workbook) {
        final Sheet facilities = workbook.getSheetAt(1);
        assertThat(facilities.getPhysicalNumberOfRows()).isEqualTo(2);
        final List<String> facilityInfo = getDataFromRow(facilities, 1);
        assertThat(facilityInfo).containsSequence(
                facility1.name.fi,
                String.join(", ", facility1.aliases),
                hub.name.fi,
                operator1.name.fi,
                translationService.translate(facility1.status),
                facility1.statusDescription.fi,
                String.format(Locale.ENGLISH, "%.4f", facility1.location.getCentroid().getX()),
                String.format(Locale.ENGLISH, "%.4f", facility1.location.getCentroid().getY()),
                "",
                "08:00 - 18:00",
                "08:00 - 18:00",
                facility1.openingHours.info.fi,
                "" + facility1.builtCapacity.entrySet().stream()
                        .filter(entry -> asList(motorCapacities).contains(entry.getKey()))
                        .mapToInt(entry -> entry.getValue())
                        .sum(),
                "" + facility1.builtCapacity.entrySet().stream()
                        .filter(entry -> asList(bicycleCapacities).contains(entry.getKey()))
                        .mapToInt(entry -> entry.getValue())
                        .sum(),
                "" + facility1.builtCapacity.getOrDefault(CAR, 0),
                "" + facility1.builtCapacity.getOrDefault(DISABLED, 0),
                "" + facility1.builtCapacity.getOrDefault(ELECTRIC_CAR, 0),
                "" + facility1.builtCapacity.getOrDefault(MOTORCYCLE, 0),
                "" + facility1.builtCapacity.getOrDefault(BICYCLE, 0),
                "" + facility1.builtCapacity.getOrDefault(BICYCLE_SECURE_SPACE, 0)
        );
    }

    private void checkHubsAndFacilities_hubInfo(Workbook workbook) {
        final Sheet hubs = workbook.getSheetAt(0);
        assertThat(hubs.getPhysicalNumberOfRows()).isEqualTo(2);

        final List<String> hubInfo = getDataFromRow(hubs, 1);
        assertThat(hubInfo).containsExactly(
                hub.name.fi,
                String.join(", ", toArray(hub.address.streetAddress.fi, hub.address.postalCode, hub.address.city.fi)),
                String.format(Locale.ENGLISH, "%.4f", hub.location.getX()),
                String.format(Locale.ENGLISH, "%.4f", hub.location.getY()),
                "" + facility1.builtCapacity.entrySet().stream()
                        .filter(entry -> asList(motorCapacities).contains(entry.getKey()))
                        .mapToInt(entry -> entry.getValue())
                        .sum(),
                "" + facility1.builtCapacity.entrySet().stream()
                        .filter(entry -> asList(bicycleCapacities).contains(entry.getKey()))
                        .mapToInt(entry -> entry.getValue())
                        .sum(),
                "" + facility1.builtCapacity.getOrDefault(CAR, 0),
                "" + facility1.builtCapacity.getOrDefault(DISABLED, 0),
                "" + facility1.builtCapacity.getOrDefault(ELECTRIC_CAR, 0),
                "" + facility1.builtCapacity.getOrDefault(MOTORCYCLE, 0),
                "" + facility1.builtCapacity.getOrDefault(BICYCLE, 0),
                "" + facility1.builtCapacity.getOrDefault(BICYCLE_SECURE_SPACE, 0),
                facility1.name.fi
        );
    }

}
