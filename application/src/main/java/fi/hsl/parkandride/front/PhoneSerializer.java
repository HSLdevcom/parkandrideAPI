// Copyright © 2015 HSL

package fi.hsl.parkandride.front;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import fi.hsl.parkandride.core.domain.Phone;

public class PhoneSerializer extends StdSerializer<Phone> {

    public PhoneSerializer() {
        super(Phone.class);
    }

    @Override
    public void serialize(Phone value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        if (value == null) {
            jgen.writeNull();
        } else {
            jgen.writeString(value.toString());
        }
    }
}
