package fi.hsl.parkandride.front;

import static fi.hsl.parkandride.core.domain.Spatial.pointSequenceOf;

import org.geolatte.geom.Polygon;

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

    public static class Point {

        private double lon;

        private double lat;

        public Point() {}

        public Point(double lon, double lat) {
            this.lon = lon;
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

    }
}
