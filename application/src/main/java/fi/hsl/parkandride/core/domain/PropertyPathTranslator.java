// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

public final class PropertyPathTranslator {

    private static final Pattern PATTERN = Pattern.compile("\\[(\\D\\w*)\\]");

    private PropertyPathTranslator() {}

    public static String translate(String input) {
        Preconditions.checkArgument(input != null);
        Preconditions.checkArgument(!isWhitespaceOnly(input));

        return PATTERN.matcher(input).replaceAll(".$1");
    }

    private static boolean isWhitespaceOnly(String input) {
        return input.length() > 0 && input.trim().isEmpty();
    }
}
