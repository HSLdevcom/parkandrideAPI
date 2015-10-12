// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import com.mysema.query.QueryException;
import com.querydsl.sql.SQLExceptionTranslator;
import fi.hsl.parkandride.core.domain.Violation;
import fi.hsl.parkandride.core.service.ValidationException;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LiipiSQLExceptionTranslator implements SQLExceptionTranslator {

    /*
     * Postgresql:
     * ERROR: duplicate key value violates unique constraint "operator_name_fi_u"
     * Detail: Key (upper(name_fi::text))=(X-PARK) already exists.
     *
     * H2:
     * Unique index or primary key violation: "OPERATOR_NAME_FI_U ON PUBLIC.OPERATOR(NAME_FI) VALUES (CAST('X-Park' AS VARCHAR_IGNORECASE), 1)"; SQL statement:
     * insert into OPERATOR (ID, NAME_FI, NAME_SV, NAME_EN)
     * values (?, ?, ?, ?) [23505-181]
     */
    private static Pattern UNIQUE_CONSTRAINT_NAME = Pattern.compile("\"[a-z]+_([a-z_]+)_u");

    @Override
    public RuntimeException translate(String sql, List<Object> bindings, SQLException e) {
        if ("23505".equals(e.getSQLState())) {
            String message = e.getMessage().toLowerCase();
            Matcher m = UNIQUE_CONSTRAINT_NAME.matcher(message);
            if (m.find()) {
                return (ValidationException)
                        new ValidationException(new Violation("Unique", m.group(1).replace('_', '.'), e.getMessage()))
                                .initCause(e);
            }
        }
        return new QueryException(e);
    }

    @Override
    public RuntimeException translate(SQLException e) {
        return new QueryException(e);
    }
}
