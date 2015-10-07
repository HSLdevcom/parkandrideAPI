// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.front;

import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.service.reporting.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static fi.hsl.parkandride.front.UrlSchema.REPORT;
import static fi.hsl.parkandride.front.UrlSchema.REPORT_ID;
import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class ReportController {
    private static final Logger log = LoggerFactory.getLogger(ReportController.class);
    public static final String MEDIA_TYPE_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final Map<String, ReportService> reporters;

    @Inject
    public ReportController(Collection<ReportService> reportServices) {
        this.reporters = reportServices.stream().collect(toMap(rs -> rs.reportName(), rs -> rs));
    }

    @RequestMapping(method = POST, value = REPORT, consumes = APPLICATION_JSON_VALUE,  produces = MEDIA_TYPE_EXCEL)
    public ResponseEntity<?> report(@NotNull @PathVariable(REPORT_ID) String reportId, @RequestBody ReportParameters parameters, User currentUser) {
        log.info("report({})", reportId);
        byte[] report = Optional.ofNullable(reporters.get(reportId)).get().generateReport(currentUser, parameters);
        return ok().header(CONTENT_DISPOSITION, "attachment; filename=\"" + reportId + "\"").body(report);
    }
}
