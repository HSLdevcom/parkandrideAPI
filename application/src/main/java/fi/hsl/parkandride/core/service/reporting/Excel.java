// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service.reporting;

import fi.hsl.parkandride.core.domain.MultilingualString;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static java.lang.Math.max;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.apache.poi.ss.usermodel.Cell.*;

class Excel {
    private static final Logger log = LoggerFactory.getLogger(Excel.class);

    private final Workbook wb = new XSSFWorkbook();
    private final Font font12pt = wb.createFont();
    private final Font font12ptGreen = wb.createFont();
    private final Font font12ptRed = wb.createFont();
    private final Font bold = wb.createFont();
    private final CellStyle title = wb.createCellStyle();
    final CellStyle text = wb.createCellStyle();
    final CellStyle multiline = wb.createCellStyle();
    final CellStyle integer = wb.createCellStyle();
    final CellStyle decimal = wb.createCellStyle();
    final CellStyle percent = wb.createCellStyle();
    final CellStyle date = wb.createCellStyle();
    final CellStyle datetime = wb.createCellStyle();
    final CellStyle month = wb.createCellStyle();

    // Colorized cells
    final CellStyle green = wb.createCellStyle();
    final CellStyle red   = wb.createCellStyle();
    final CellStyle yellow = wb.createCellStyle();
    final CellStyle orange = wb.createCellStyle();

    private final DataFormat df = wb.createDataFormat();
    private Sheet sheet;

    {
        font12pt.setFontHeightInPoints((short) 12);
        font12ptGreen.setFontHeightInPoints((short) 12);
        font12ptGreen.setColor(IndexedColors.GREEN.index);
        font12ptRed.setFontHeightInPoints((short) 12);
        font12ptRed.setColor(IndexedColors.RED.index);
        bold.setFontHeightInPoints((short) 12);
        bold.setBoldweight(Font.BOLDWEIGHT_BOLD);
        title.setFont(bold);
        text.setDataFormat(df.getFormat("TEXT"));
        text.setFont(font12pt);
        multiline.setDataFormat(df.getFormat("TEXT"));
        multiline.setFont(font12pt);
        multiline.setWrapText(true);
        integer.setDataFormat(df.getFormat("0"));
        integer.setFont(font12pt);
        decimal.setDataFormat(df.getFormat("#,####0.0000"));
        decimal.setFont(font12pt);
        percent.setDataFormat(df.getFormat("0.00 %"));
        percent.setFont(font12pt);
        date.setDataFormat(df.getFormat("d.M.yyyy"));
        date.setFont(font12pt);
        datetime.setDataFormat(df.getFormat("d.M.yyyy HH:mm"));
        datetime.setFont(font12pt);
        month.setDataFormat(df.getFormat("M\\/yyyy"));
        month.setFont(font12pt);

        // Colorized
        green.setFont(font12ptGreen);
        green.setFillPattern(CellStyle.SOLID_FOREGROUND);
        green.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        red.setFont(font12ptRed);
        yellow.setFont(font12pt);
        yellow.setFillPattern(CellStyle.SOLID_FOREGROUND);
        yellow.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
        orange.setFont(font12pt);
        orange.setFillPattern(CellStyle.SOLID_FOREGROUND);
        orange.setFillForegroundColor(IndexedColors.CORAL.index);
    }

    static class TableColumn<T> {
        static <T> TableColumn<T> col(String name, Function<T, Object> valueFunction) {
            return col(name, valueFunction, val -> null);
        }

        static <T> TableColumn<T> col(String name, Function<T, Object> valueFunction, CellStyle style) {
            return col(name, valueFunction, v -> style);
        }

        static <T> TableColumn<T> col(String name, Function<T, Object> valueFunction, Function<T, CellStyle> styleFn) {
            return new TableColumn<>(name, valueFunction, styleFn);
        }

        public final String name;
        public final Function<T, Object> valueFunction;
        public final Function<T, CellStyle> styleFn;

        private TableColumn(String name, Function<T, Object> valueFunction, Function<T, CellStyle> styleFn) {
            this.name = name;
            this.valueFunction = valueFunction;
            this.styleFn = styleFn;
        }
    }

    public <T> void addSheet(String name, List<T> rows, List<TableColumn<T>> columns) {
        sheet = wb.createSheet(name);
        sheet.createFreezePane(0, 1, 0, 1);

        int maxColumns = 0;
        Row headerRow = sheet.createRow(0);
        for (int column = 0; column < columns.size(); ++column, maxColumns = max(maxColumns, column)) {
            Cell cell = headerRow.createCell(column, CELL_TYPE_STRING);
            TableColumn<T> colType = columns.get(column);
            cell.setCellStyle(title);
            cell.setCellValue(colType.name);
        }

        for (int r = 0; r < rows.size(); ++r) {
            Row row = sheet.createRow(r + 1);
            for (int column = 0; column < columns.size(); ++column) {
                TableColumn<T> colType = columns.get(column);
                Object value;
                Optional<CellStyle> style;
                final T v = rows.get(r);
                try {
                  value = colType.valueFunction.apply(v);
                } catch (RuntimeException ex) {
                  log.error("Failed to generate cell for column " + colType.name, ex);
                  value = cleanExceptionMessage(ex);
                }
                style = Optional.ofNullable(colType.styleFn.apply(v));
                if (value == null) {
                    row.createCell(column, CELL_TYPE_BLANK);
                } else if (value instanceof Double) {
                    Cell cell = row.createCell(column, CELL_TYPE_NUMERIC);
                    cell.setCellStyle(style.orElse(decimal));
                    cell.setCellValue((Double) value);
                } else if (value instanceof Integer) {
                    Cell cell = row.createCell(column, CELL_TYPE_NUMERIC);
                    cell.setCellStyle(style.orElse(integer));
                    cell.setCellValue((Integer) value);
                } else if (value instanceof MultilingualString) {
                    Cell cell = row.createCell(column, CELL_TYPE_STRING);
                    cell.setCellStyle(style.orElse(text));
                    cell.setCellValue(((MultilingualString) value).fi);
                } else if (value instanceof LocalDate) {
                    Cell cell = row.createCell(column, CELL_TYPE_NUMERIC);
                    cell.setCellStyle(style.orElse(date));
                    cell.setCellValue(((LocalDate) value).toDate());
                } else if (value instanceof DateTime) {
                    Cell cell = row.createCell(column, CELL_TYPE_NUMERIC);
                    cell.setCellStyle(style.orElse(datetime));
                    cell.setCellValue(((DateTime) value).toDate());
                } else if (value instanceof Collection) {
                    // currently must be last item in list
                    for (Object o : (Collection<?>) value) {
                        Cell cell = row.createCell(column++, CELL_TYPE_STRING);
                        cell.setCellStyle(text);
                        cell.setCellValue(o.toString());
                    }
                } else {
                    Cell cell = row.createCell(column, CELL_TYPE_STRING);
                    String val = value.toString();
                    if (style.isPresent()) {
                        cell.setCellStyle(style.get());
                    } else if (val.indexOf('\n') > 0) {
                        cell.setCellStyle(multiline);
                    } else {
                        cell.setCellStyle(text);
                    }
                    cell.setCellValue(value.toString());
                }
            }
        }
        if (!rows.isEmpty()) {
            sheet.setAutoFilter(new CellRangeAddress(0, rows.size(), 0, maxColumns));
        }
        autosize(maxColumns);
    }

    private static String cleanExceptionMessage(RuntimeException ex) {
        return asList(getStackTraceAsString(ex).split("\n")).stream().filter(l -> !l.matches(".*(sun\\.reflect|java\\.lang\\.reflect|\\$\\$Lambda\\$.*Unknown Source).*")).limit(10).collect(joining("\n"));
    }

    public <T> void addSheet(String name, String... textRows) {
        sheet = wb.createSheet(name);
        int row = 0;
        for (String txt : textRows) {
            Cell cell = sheet.createRow(row++).createCell(0, CELL_TYPE_STRING);
            cell.setCellStyle(text);
            cell.setCellValue(txt);
        }
    }

    private void autosize(int maxColumns) {
        try {
            for (int i = 0; i < maxColumns; ++i) {
                sheet.autoSizeColumn(i);
            }
        } catch (Throwable t) {
            log.warn("Failed to evaluate excel cell widths", t);
        }
    }

    byte[] toBytes() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
            wb.write(out);
            out.close();
            wb.close();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}