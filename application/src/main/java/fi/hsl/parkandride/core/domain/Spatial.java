package fi.hsl.parkandride.core.domain;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.codec.Wkt;
import org.geolatte.geom.crs.CrsId;

public class Spatial {

    public static final CrsId WGS84 = CrsId.valueOf(4326);

    public static Geometry fromWkt(String wktShape) {
        return Wkt.newDecoder(Wkt.Dialect.POSTGIS_EWKT_1).decode(normalize(wktShape));
    }

    public static String toWkt(Geometry geometry) {
        return Wkt.newEncoder(Wkt.Dialect.POSTGIS_EWKT_1).encode(geometry);
    }

    public static Polygon fromWktPolygon(String wktShape) {
        return (Polygon) Wkt.newDecoder(Wkt.Dialect.POSTGIS_EWKT_1).decode(normalize(wktShape));
    }

    private static String normalize(String wkt) {
        if (!wkt.startsWith("SRID=")) {
            return "SRID=4326;" + wkt;
        } else {
            return wkt;
        }
    }
}
