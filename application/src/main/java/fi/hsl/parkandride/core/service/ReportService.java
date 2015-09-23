// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.front.ReportParameters;

public interface ReportService {

    /**
     * Generates the Excel report and converts it to bytes.
     * Verifies that the current user has permission to generate reports.
     */
    byte[] generateReport(User currentUser, ReportParameters reportParameters);

    /**
     * Get the name of the report this service produces. Names should be unique.
     * The name is used when querying for the report in the REST interface
     */
    String reportName();
}
