// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service.reporting;

import fi.hsl.parkandride.core.back.RequestLogRepository;
import fi.hsl.parkandride.core.domain.Permission;
import fi.hsl.parkandride.core.domain.RequestLogEntry;
import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.front.ReportParameters;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;

import static fi.hsl.parkandride.core.service.AuthenticationService.authorize;
import static fi.hsl.parkandride.core.service.reporting.ReportServiceSupport.FINNISH_DATE_FORMAT;
import static fi.hsl.parkandride.util.ArgumentValidator.validate;
import static java.util.Arrays.asList;

public class RequestLogReportService implements ReportService {

    private static final String REPORT_NAME = "RequestLog";

    @Inject
    ExcelUtil excelUtil;

    @Inject
    RequestLogRepository requestLogRepository;

    @Override
    public byte[] generateReport(User currentUser, ReportParameters reportParameters) {
        authorize(currentUser, Permission.REPORT_GENERATE);
        Excel excel = new Excel();

        addRequestLogSheet(excel, getRowsForDates(
            validate(reportParameters.startDate).notNull(),
            validate(reportParameters.endDate).notNull()
        ));

        excel.addSheet(excelUtil.getMessage("reports.requestlog.sheets.legend"),
                excelUtil.getMessage("reports.requestlog.legend").split("\n"));

        return excel.toBytes();
    }

    private List<RequestLogEntry> getRowsForDates(String startDate, String endDate) {
        final DateTime start = FINNISH_DATE_FORMAT.parseLocalDate(startDate).toDateTimeAtStartOfDay();
        final DateTime end = FINNISH_DATE_FORMAT.parseLocalDate(endDate).toDateTimeAtStartOfDay().millisOfDay().withMaximumValue();
        validate(start).lte(end);
        return requestLogRepository.getLogEntriesBetween(start, end);
    }

    @Override
    public String reportName() {
        return REPORT_NAME;
    }

    private void addRequestLogSheet(Excel excel, List<RequestLogEntry> logRows) {
        excel.addSheet(excelUtil.getMessage("reports.requestlog.sheets.log"), logRows, asList(
                excelUtil.tcol("reports.requestlog.col.timestamp",  (RequestLogEntry entry) -> entry.key.timestamp),
                excelUtil.tcol("reports.requestlog.col.source",     (RequestLogEntry entry) -> entry.key.source),
                excelUtil.tcol("reports.requestlog.col.url",        (RequestLogEntry entry) -> entry.key.urlPattern),
                excelUtil.tcol("reports.requestlog.col.count",      (RequestLogEntry entry) -> entry.count.intValue())
        ));
    }
}
