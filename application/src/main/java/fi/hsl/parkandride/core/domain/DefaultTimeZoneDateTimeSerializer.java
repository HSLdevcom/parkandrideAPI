// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer;
import com.fasterxml.jackson.datatype.joda.ser.JacksonJodaFormat;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

public class DefaultTimeZoneDateTimeSerializer extends JsonSerializer<DateTime> {

    @Override
    public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        DateTimeSerializer serializer = new DateTimeSerializer(formatterWithTimeZone(DateTimeZone.getDefault()));
        serializer.serialize(value, jgen, provider);
    }

    private static JacksonJodaFormat formatterWithTimeZone(DateTimeZone timeZone) {
        DateTimeFormatter formatter = ISODateTimeFormat.dateTime().withZone(timeZone);
        return new JacksonJodaFormat(new JacksonJodaFormat(formatter), timeZone.toTimeZone());
    }
}
