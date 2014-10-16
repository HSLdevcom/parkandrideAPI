package fi.hsl.parkandride.core.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

public class PropertyPathTranslator {
    private interface RegEx {
        String HEAD = "[^\\[]+";        // everything up to a [
        String INDEX = "\\[(\\w*)\\]";
        String TAIL = "\\.(.*)";        // processed recursively

        interface Group {
            int HEAD = 1;
            int INDEX = 3;
            int TAIL = 5;
        }
    }

    private static final Pattern PATH = Pattern.compile("(" + RegEx.HEAD + ")(" + RegEx.INDEX + ")(" + RegEx.TAIL + ")*");

    public String translate(String input) {
        if (!Strings.isNullOrEmpty(input) && input.trim().length() > 0) {
            String tail = input;
            StringBuilder translated = new StringBuilder();

            Matcher m = PATH.matcher(tail);
            while (m.matches()) {
                translated.append(m.group(RegEx.Group.HEAD)).append(".").append(m.group(RegEx.Group.INDEX));

                tail = m.group(RegEx.Group.TAIL);
                if (tail == null) {
                   return translated.toString();
                }
                translated.append(".");
                m = PATH.matcher(tail);
            }
            return translated.append(tail).toString();
        }
        return input;
    }
}
