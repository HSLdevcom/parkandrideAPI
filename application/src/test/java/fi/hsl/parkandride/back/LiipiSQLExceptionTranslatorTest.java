// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import fi.hsl.parkandride.core.service.ValidationException;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(Enclosed.class)
public class LiipiSQLExceptionTranslatorTest {

    protected static abstract class Base {

        public static final String EX2_MESSAGE = "Exception 2 message 23985398";

        SQLException ex1; // produced with stacktrace by throwing the excpetion inside createEx1()
        SQLException ex2 = new SQLException(EX2_MESSAGE);

        LiipiSQLExceptionTranslator translator = new LiipiSQLExceptionTranslator();

        protected void createEx1(String message, String SQLState) {
            try {
                throw new SQLException(message, SQLState);
            } catch (SQLException e) {
                ex1 = e;
            }
            ex1.setNextException(ex2);
        }

        protected abstract RuntimeException translateUsingTestedMethod(SQLException e);

        @Test
        public void verify_that_ex2_is_not_visible_in_ex1_stacktrace_by_default() {
            // This test verifies that the chained exceptions of SQLException (which are retrieved with
            // getNextException) are not visible in stack trace if nothing is done. Other test cases
            // verify that adding the chained exceptions as suppressed exceptions makes them visible
            // in stack trace (and thus also in logs).
            createEx1("Test exception", null);
            assertThat(getPrintedStackTrace(ex1), not(containsString(EX2_MESSAGE)));
        }

        @Test
        public void translator_copies_chained_exception_to_suppressed_exception() {
            createEx1("Test exception", null);
            RuntimeException translatedEx1 = translateUsingTestedMethod(ex1);
            final Throwable[] suppressedExceptions = translatedEx1.getCause().getSuppressed();
            assertThat(suppressedExceptions.length, is(1));
            assertThat(suppressedExceptions[0], equalTo(ex2));
        }

        @Test
        public void chained_exceptions_are_visible_in_toString_output_of_translated_exception() {
            createEx1("Test exception", null);
            RuntimeException translatedEx1 = translateUsingTestedMethod(ex1);
            assertThat(getPrintedStackTrace(translatedEx1), containsString(EX2_MESSAGE));
        }

        protected String getPrintedStackTrace(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            printWriter.flush();
            return stringWriter.toString();
        }
    }

    public static final class Translate_with_sql_and_binding_parameters extends Base {
        private static final List<Object> BINDINGS = new ArrayList<>();

        @Override
        protected RuntimeException translateUsingTestedMethod(SQLException e) {
            return translator.translate("select * from test", BINDINGS, e);
        }

        @Test
        public void chained_exceptions_are_visible_also_in_validation_exception_stack_trace() {
            createEx1("Test exception with unique constraint \"abc_def_u\" violated", "23505");
            RuntimeException translatedEx1 = translateUsingTestedMethod(ex1);
            assertThat(translatedEx1, instanceOf(ValidationException.class));
            assertThat(getPrintedStackTrace(translatedEx1), containsString(EX2_MESSAGE));
        }
    }

    public static final class Translate_with_just_exception_parameter extends Base {
        @Override
        protected RuntimeException translateUsingTestedMethod(SQLException e) {
            return translator.translate(e);
        }
    }
}
