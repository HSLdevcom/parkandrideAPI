package fi.hsl.parkandride.inbound;

import static fi.hsl.parkandride.core.domain.Spatial.WGS84;
import static fi.hsl.parkandride.core.domain.Spatial.pointSequenceOf;

import org.geolatte.geom.DimensionalFlag;
import org.geolatte.geom.PointSequence;
import org.geolatte.geom.PointSequenceBuilders;
import org.geolatte.geom.Polygon;

import fi.hsl.parkandride.core.domain.Spatial;

public class BBox {

    private Point min = new Point();

    private Point max = new Point();

    public Point getMin() {
        return min;
    }

    public void setMin(Point min) {
        this.min = min;
    }

    public Point getMax() {
        return max;
    }

    public void setMax(Point max) {
        this.max = max;
    }

    public Polygon toPolygon() {
        return new Polygon(pointSequenceOf(
                min.getLon(), min.getLat(),
                min.getLon(), max.getLat(),
                max.getLon(), max.getLat(),
                max.getLon(), min.getLat(),
                min.getLon(), min.getLat()));
    }
}
