// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.front;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.service.ReportService;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static fi.hsl.parkandride.front.UrlSchema.REPORT;
import static fi.hsl.parkandride.front.UrlSchema.REPORT_ID;

@RestController
public class ReportController {
    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    @Inject
    ReportService reportService;

    @RequestMapping(method = POST, value = REPORT, consumes = APPLICATION_JSON_VALUE,  produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<?> report(@PathVariable(REPORT_ID) String reportId, @RequestBody ReportParameters parameters, User currentUser) {
        log.info("report({})", reportId);
        String reportName = reportId.replaceAll("[.-].*", "");
        byte[] report;
            try {
                report = (byte[]) reportService.getClass().getMethod("report" + reportName, User.class, ReportParameters.class).invoke(reportService, currentUser, parameters);
            } catch (NoSuchMethodException e) {
                log.info("Invalid report requested: " + reportName);
                return ResponseEntity.notFound().build();
            } catch (Exception e) {
                log.error("Failed to generate report", e);
                return badRequest().body(new byte[0]);
            }
        return ok().header(CONTENT_DISPOSITION, "attachment; filename=\"" + reportId + "\"").body(report);
    }
}
