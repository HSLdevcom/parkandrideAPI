// Copyright © 2015 HSL

package fi.hsl.parkandride.front.geojson;

import java.util.Map;

import org.geolatte.geom.Geometry;

public class Feature {

    public Long id;

    public Geometry geometry;

    public Map<String, Object> properties;


    public Long getId() {
        return id;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public String getType() {
        return "Feature";
    }
}
