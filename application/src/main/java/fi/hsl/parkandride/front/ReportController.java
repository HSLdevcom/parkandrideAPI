// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.front;

import static fi.hsl.parkandride.front.UrlSchema.REPORT;
import static fi.hsl.parkandride.front.UrlSchema.REPORT_ID;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.service.ReportService;

@RestController
public class ReportController {
    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    @Inject
    ReportService reportService;

    @RequestMapping(method = GET, value = REPORT,  produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<?> getOperator(@PathVariable(REPORT_ID) String reportId, User currentUser) {
        log.info("report({})", reportId);
        String reportName = reportId.replaceAll("[.-].*", "");
        byte[] report;
            try {
                report = (byte[]) reportService.getClass().getMethod("report" + reportName, User.class).invoke(reportService, currentUser);
            } catch (NoSuchMethodException e) {
                return ResponseEntity.notFound().build();
            } catch (Exception e) {
                return badRequest().body(new byte[0]);
            }
        return ok().header(CONTENT_DISPOSITION, "attachment; filename=\"" + reportId + "\"").body(report);
    }
}
