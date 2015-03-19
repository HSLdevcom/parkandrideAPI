// Copyright © 2015 HSL

package fi.hsl.parkandride.back;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mysema.query.QueryException;
import com.mysema.query.sql.SQLExceptionTranslator;

import fi.hsl.parkandride.core.domain.Violation;
import fi.hsl.parkandride.core.service.ValidationException;

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
                return new ValidationException(new Violation("Unique", m.group(1).replace('_', '.'), e.getMessage()));
            }
        }
        return new QueryException(e);
    }

    @Override
    public RuntimeException translate(SQLException e) {
        return new QueryException(e);
    }

}
