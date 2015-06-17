// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import java.io.IOException;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.DateTimeDeserializer;

public class StrictIsoDateTimeDeserializer extends JsonDeserializer<DateTime> {

    private static final String DATE = "\\d{4}+-\\d{2}+-\\d{2}+";
    private static final String TIME = "\\d{2}+:\\d{2}+:\\d{2}+";
    private static final String FRACTIONS = "([,\\.]\\d+)?";
    private static final String ZONE = "(Z|[+-]\\d{2}+:\\d{2}+)";
    private static final Pattern PATTERN = Pattern.compile(DATE + "T" + TIME + FRACTIONS + ZONE);

    private final DateTimeDeserializer dateTimeDeserializer = new DateTimeDeserializer(DateTime.class);

    @Override
    public DateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken token = jp.getCurrentToken();
        if (token == JsonToken.VALUE_STRING) {
            String str = jp.getText().trim();
            if (!isValid(str)) {
                throw ctxt.mappingException("expected ISO 8601 date time with timezone, " +
                        "for example \"" + DateTime.now() + "\", but got \"" + str + "\"");
            }
            return (DateTime) dateTimeDeserializer.deserialize(jp, ctxt);
        }
        throw ctxt.mappingException(dateTimeDeserializer.handledType(), token);
    }

    public boolean isValid(String str) {
        return StringUtils.isEmpty(str) || PATTERN.matcher(str).matches();
    }
}
