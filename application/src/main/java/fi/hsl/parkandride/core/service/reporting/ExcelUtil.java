// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service.reporting;

import fi.hsl.parkandride.core.domain.TimeDuration;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.function.Function;

import static java.lang.String.format;

public class ExcelUtil {

    private static final Locale DEFAULT_LOCALE = new Locale("fi");
    private final MessageSource messageSource;

    public ExcelUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    static String time(TimeDuration time) {
        return time == null ? null : format("%02d:%02d - %02d:%02d", time.from.getHour(), time.from.getMinute(), time.until.getHour(), time.until.getMinute());
    }

    String getMessage(String code) {
        return messageSource.getMessage(code, null, DEFAULT_LOCALE);
    }

    /**
     * Adds translated column
     * @param key
     * @param valFn
     **/
    <T> Excel.TableColumn<T> tcol(String key, Function<T, Object> valFn) {
        return Excel.TableColumn.col(getMessage(key), valFn);
    }

    <T> Excel.TableColumn<T> tcol(String key, Function<T, Object> valFn, CellStyle cellStyle) {
        return Excel.TableColumn.col(getMessage(key), valFn, cellStyle);
    }
}
