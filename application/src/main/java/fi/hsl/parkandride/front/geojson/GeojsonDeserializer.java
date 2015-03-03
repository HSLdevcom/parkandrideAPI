package fi.hsl.parkandride.front.geojson;

import java.io.IOException;

import org.geolatte.common.dataformats.json.jackson.JsonException;
import org.geolatte.common.dataformats.json.jackson.JsonMapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * This is a bridge deserializer for Jackson 1.9 used by Geolatte and Jackson 2.x.
 */
public class GeojsonDeserializer<T> extends JsonDeserializer<T> {

    private final JsonMapper jsonMapper;

    private final Class<T> type;

    public GeojsonDeserializer(JsonMapper jsonMapper, Class<T> type) {
        this.jsonMapper = jsonMapper;
        this.type = type;
    }

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        try {
            return jsonMapper.fromJson(jp.readValueAsTree().toString(), type);
        } catch (JsonException e) {
            throw new JsonMappingException(e.getMessage(), jp.getCurrentLocation(), e.getCause());
        }
    }

}
