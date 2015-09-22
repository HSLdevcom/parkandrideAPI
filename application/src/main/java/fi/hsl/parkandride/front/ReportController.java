// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.front;

import com.google.common.collect.ImmutableMap;
import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.service.AccessDeniedException;
import fi.hsl.parkandride.core.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.function.BiFunction;

import static fi.hsl.parkandride.front.ReportController.ReportType.FacilityUsage;
import static fi.hsl.parkandride.front.ReportController.ReportType.HubsAndFacilities;
import static fi.hsl.parkandride.front.ReportController.ReportType.MaxUtilization;
import static fi.hsl.parkandride.front.UrlSchema.REPORT;
import static fi.hsl.parkandride.front.UrlSchema.REPORT_ID;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class ReportController {
    private static final Logger log = LoggerFactory.getLogger(ReportController.class);
    public static final String MEDIA_TYPE_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public enum ReportType {
        HubsAndFacilities,
        FacilityUsage,
        MaxUtilization
    }

    private final Map<ReportType, BiFunction<User, ReportParameters, byte[]>> reporters;

    @Inject
    public ReportController(final ReportService reportService) {
        this.reporters = ImmutableMap.<ReportType, BiFunction<User, ReportParameters, byte[]>> builder()
                .put(HubsAndFacilities, reportService::reportHubsAndFacilities)
                .put(FacilityUsage, reportService::reportFacilityUsage)
                .put(MaxUtilization, reportService::reportMaxUtilization)
                .build();
    }

    @RequestMapping(method = POST, value = REPORT, consumes = APPLICATION_JSON_VALUE,  produces = MEDIA_TYPE_EXCEL)
    public ResponseEntity<?> report(@NotNull @PathVariable(REPORT_ID) ReportType reportId, @RequestBody ReportParameters parameters, User currentUser) {
        log.info("report({})", reportId);
        byte[] report;
        try {
            report = reporters.get(reportId).apply(currentUser, parameters);
        } catch (AccessDeniedException ade) {
            log.error("Access denied", ade);
            return status(HttpStatus.FORBIDDEN).body(new byte[0]);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to generate report", e);
            return badRequest().body(new byte[0]);
        }
        return ok().header(CONTENT_DISPOSITION, "attachment; filename=\"" + reportId + "\"").body(report);
    }
}
