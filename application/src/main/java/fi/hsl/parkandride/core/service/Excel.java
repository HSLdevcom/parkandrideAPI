package fi.hsl.parkandride.core.service;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.apache.poi.ss.usermodel.Cell.*;

import fi.hsl.parkandride.core.domain.MultilingualString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Math.max;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

class Excel {
    private static final Logger log = LoggerFactory.getLogger(Excel.class);

    private final Workbook wb = new HSSFWorkbook();
    private final Font font12pt = wb.createFont();
    private final Font bold = wb.createFont();
    private final CellStyle title = wb.createCellStyle();
    private final CellStyle text = wb.createCellStyle();
    private final CellStyle multiline = wb.createCellStyle();
    private final CellStyle integer = wb.createCellStyle();
    private final CellStyle decimal = wb.createCellStyle();
    private final DataFormat df = wb.createDataFormat();
    private Sheet sheet;

    {
        font12pt.setFontHeightInPoints((short) 12);
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
    }

    static class TableColumn<T> {
        static <T> TableColumn<T> col(String name, Function<T, Object> valueFunction) {
            return new TableColumn<>(name, valueFunction);
        }

        public final String name;
        public final Function<T, Object> valueFunction;

        private TableColumn(String name, Function<T, Object> valueFunction) {
            this.name = name;
            this.valueFunction = valueFunction;
        }
    }

    public <T> void addSheet(String name, List<T> rows, List<TableColumn<T>> columns) {
        sheet = wb.createSheet(name);
        sheet.createFreezePane(0, 1, 0, 1);

        int maxColumns = 0;
        Row headerRow = sheet.createRow(0);
        for (int colunm = 0; colunm < columns.size(); ++colunm, maxColumns = max(maxColumns, colunm)) {
            Cell cell = headerRow.createCell(colunm, CELL_TYPE_STRING);
            TableColumn<T> colType = columns.get(colunm);
            cell.setCellStyle(title);
            cell.setCellValue(colType.name);
        }

        for (int r = 0; r < rows.size(); ++r) {
            Row row = sheet.createRow(r + 1);
            for (int column = 0; column < columns.size(); ++column) {
                TableColumn<T> colType = columns.get(column);
                Object value;
                try {
                  value = colType.valueFunction.apply(rows.get(r));
                } catch (RuntimeException ex) {
                  log.error("Failed to generate cell for column " + colType.name, ex);
                  value = cleanExceptionMessage(ex);
                }
                if (value == null) {
                    row.createCell(column, CELL_TYPE_BLANK);
                } else if (value instanceof Double) {
                    Cell cell = row.createCell(column, CELL_TYPE_NUMERIC);
                    cell.setCellStyle(decimal);
                    cell.setCellValue((Double) value);
                } else if (value instanceof Integer) {
                    Cell cell = row.createCell(column, CELL_TYPE_NUMERIC);
                    cell.setCellStyle(integer);
                    cell.setCellValue((Integer) value);
                } else if (value instanceof MultilingualString) {
                    Cell cell = row.createCell(column, CELL_TYPE_STRING);
                    cell.setCellStyle(text);
                    cell.setCellValue(((MultilingualString) value).fi);
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
                    if (val.indexOf('\n') > 0) {
                        cell.setCellStyle(multiline);
                    } else {
                        cell.setCellStyle(text);
                    }
                    cell.setCellValue(value.toString());
                }
            }
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