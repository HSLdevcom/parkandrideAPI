package fi.hsl.parkandride.core.domain;

import org.geolatte.geom.*;
import org.geolatte.geom.codec.Wkt;
import org.geolatte.geom.crs.CrsId;

public class Spatial {

    public static final CrsId WGS84 = CrsId.valueOf(4326);

    public static PointSequence pointSequenceOf(double... coordinates) {
        if (coordinates.length % 2 != 0) {
            throw new IllegalArgumentException("Expected 2D coordinates");
        }
        PointSequenceBuilder builder = PointSequenceBuilders.fixedSized(coordinates.length / 2, DimensionalFlag.d2D, WGS84);
        for (int i = 0; i+1 < coordinates.length; i++) {
            builder.add(coordinates[i], coordinates[i+1]);
        }
        return builder.toPointSequence();
    }

    public static Geometry fromWkt(String wktShape) {
        return Wkt.newDecoder(Wkt.Dialect.POSTGIS_EWKT_1).decode(wktShape);
    }

    public static String toWkt(Geometry geometry) {
        return Wkt.newEncoder(Wkt.Dialect.POSTGIS_EWKT_1).encode(geometry);
    }

    public static Polygon fromWktPolygon(String wktShape) {
        return (Polygon) Wkt.newDecoder(Wkt.Dialect.POSTGIS_EWKT_1).decode(wktShape);
    }

}
