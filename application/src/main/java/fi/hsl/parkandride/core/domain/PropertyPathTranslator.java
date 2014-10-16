package fi.hsl.parkandride.core.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyPathTranslator {
    private interface RegEx {
        String HEAD = "[^\\[\\.]+";     // everything up to a [ or .
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
        Matcher matcher = PATH.matcher(input);
        if (matcher.matches()) {
            return new StringBuilder(matcher.group(RegEx.Group.HEAD))
                    .append(".")
                    .append(matcher.group(RegEx.Group.INDEX))
                    .append(".")
                    .append(translate(matcher.group(RegEx.Group.TAIL)))
                    .toString();
        }
        return input;
    }
}
