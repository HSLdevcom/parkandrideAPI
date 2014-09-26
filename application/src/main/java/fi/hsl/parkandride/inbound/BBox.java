package fi.hsl.parkandride.inbound;

import org.geolatte.geom.DimensionalFlag;
import org.geolatte.geom.PointSequence;
import org.geolatte.geom.PointSequenceBuilders;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.crs.CrsId;

public class BBox {

    public static int WGS84 = 4326;

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
        PointSequence points = PointSequenceBuilders.fixedSized(5, DimensionalFlag.d2D, CrsId.valueOf(WGS84))
                .add(min.getLon(), min.getLat())
                .add(min.getLon(), max.getLat())
                .add(max.getLon(), max.getLat())
                .add(max.getLon(), min.getLat())
                .add(min.getLon(), min.getLat())
                .toPointSequence();
        return new Polygon(points);
    }
}
