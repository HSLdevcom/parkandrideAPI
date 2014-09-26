package fi.hsl.parkandride.inbound;

import java.io.IOException;

import org.geolatte.common.dataformats.json.jackson.JsonException;
import org.geolatte.common.dataformats.json.jackson.JsonMapper;
import org.geolatte.geom.Geometry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class GeometryDeserializer extends JsonDeserializer<Geometry> {

    private final JsonMapper jsonMapper;

    public GeometryDeserializer(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public Geometry deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        try {
            return jsonMapper.fromJson(jp.readValueAsTree().toString(), Geometry.class);
        } catch (JsonException e) {
            throw new IOException(e);
        }
    }

}
