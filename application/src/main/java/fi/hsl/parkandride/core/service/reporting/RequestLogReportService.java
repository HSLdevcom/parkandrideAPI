// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service.reporting;

import fi.hsl.parkandride.core.back.RequestLogRepository;
import fi.hsl.parkandride.core.domain.Permission;
import fi.hsl.parkandride.core.domain.RequestLogEntry;
import fi.hsl.parkandride.core.domain.RequestLogKey;
import fi.hsl.parkandride.core.domain.User;
import org.apache.poi.ss.usermodel.CellStyle;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static fi.hsl.parkandride.core.service.AuthenticationService.authorize;
import static fi.hsl.parkandride.core.service.reporting.ReportServiceSupport.FINNISH_DATE_FORMAT;
import static fi.hsl.parkandride.util.ArgumentValidator.validate;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

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

        final RequestLogInterval interval = Optional.ofNullable(reportParameters.requestLogInterval).orElse(RequestLogInterval.DAY);
        addRequestLogSheet(excel, getRowsForDates(
                validate(reportParameters.startDate).notNull(),
                validate(reportParameters.endDate).notNull(),
                interval
        ), interval);

        excel.addSheet(excelUtil.getMessage("reports.requestlog.sheets.legend"),
                excelUtil.getMessage("reports.requestlog.legend").split("\n"));

        return excel.toBytes();
    }

    private List<RequestLogEntry> getRowsForDates(String startDate, String endDate, RequestLogInterval interval) {
        final DateTime start = FINNISH_DATE_FORMAT.parseLocalDate(startDate).toDateTimeAtStartOfDay();
        final DateTime end = FINNISH_DATE_FORMAT.parseLocalDate(endDate).toDateTimeAtStartOfDay().millisOfDay().withMaximumValue();
        validate(start).lte(end);
        final List<RequestLogEntry> logEntriesBetween = requestLogRepository.getLogEntriesBetween(start, end);
        return logEntriesBetween.stream()
                .collect(groupingBy(entry -> interval.apply(entry.key)))
                .entrySet().stream()
                .map(groupedEntry -> {
                    final RequestLogKey key = groupedEntry.getKey();
                    final Long totalCount = groupedEntry.getValue().stream().collect(summingLong(entry -> entry.count));
                    return new RequestLogEntry(key, totalCount);
                })
                .sorted(comparing(entry -> entry.key))
                .collect(toList());
    }

    @Override
    public String reportName() {
        return REPORT_NAME;
    }

    private void addRequestLogSheet(Excel excel, List<RequestLogEntry> logRows, RequestLogInterval interval) {
        final String unknownSource = excelUtil.getMessage("reports.requestlog.unknownSource");
        excel.addSheet(excelUtil.getMessage("reports.requestlog.sheets.log"), logRows, asList(
                excelUtil.tcol("reports.requestlog.col.timestamp." + interval,  (RequestLogEntry entry) -> entry.key.timestamp, cellStyleFor(excel, interval)),
                excelUtil.tcol("reports.requestlog.col.source",     (RequestLogEntry entry) -> Optional.ofNullable(entry.key.source).orElse(unknownSource)),
                excelUtil.tcol("reports.requestlog.col.url",        (RequestLogEntry entry) -> entry.key.urlPattern),
                excelUtil.tcol("reports.requestlog.col.count",      (RequestLogEntry entry) -> entry.count.intValue())
        ));
    }

    private CellStyle cellStyleFor(Excel excel, RequestLogInterval interval) {
        if (interval == RequestLogInterval.HOUR) {
            return excel.datetime;
        } else if (interval == RequestLogInterval.DAY) {
            return excel.date;
        } else {
            return excel.month;
        }

    }
}
