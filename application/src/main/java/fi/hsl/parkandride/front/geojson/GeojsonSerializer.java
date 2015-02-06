package fi.hsl.parkandride.front.geojson;

import java.io.IOException;

import org.geolatte.common.dataformats.json.jackson.JsonException;
import org.geolatte.common.dataformats.json.jackson.JsonMapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * This is a bridge serializer for Jackson 1.9 used by Geolatte and Jackson 2.x.
 */
public class GeojsonSerializer<T> extends JsonSerializer<T> {

    private final JsonMapper jsonMapper;

    public GeojsonSerializer(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void serialize(T value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        try {
//            jgen.writeRaw(':');
            jgen.writeRawValue(jsonMapper.toJson(value));
        } catch (JsonException e) {
            throw new IOException(e);
        }
    }
}
