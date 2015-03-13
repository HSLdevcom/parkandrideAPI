package fi.hsl.parkandride.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.geolatte.geom.*;
import org.junit.Test;


public class SpatialTest {

    @Test(expected = IllegalArgumentException.class)
    public void illegal_polygon() {
        Spatial.fromWktPolygon("POLYGON( 24.881973486328125 60.15671327918588, 24.936218481445312 60.160642536827424, 24.978103857421875 60.16696253094966, 24" +
                ".881973486328125 60.15671327918588 )");
    }

    @Test
    public void point() {
        assertPoint((Point) Spatial.parseWKT("POINT(0.0123 123)"), 0.0123, 123);
    }

    @Test
    public void negative_coordinates() {
        assertPoint((Point) Spatial.parseWKT("POINT(-0.1 -123)"), -0.1, -123);
    }

    @Test
    public void lineString() {
        LineString ls = (LineString) Spatial.parseWKT("\tLINESTRING (30.0 10.10, 10 30, 40 40)\n");
        assertPoint(ls.getPointN(0), 30, 10.1);
        assertPoint(ls.getPointN(1), 10, 30);
        assertPoint(ls.getPointN(2), 40, 40);
    }

    @Test
    public void polygon() {
        Polygon polygon = (Polygon) Spatial.parseWKT("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))");
        assertThat(polygon.getNumInteriorRing()).isEqualTo(0);
        assertPoint(polygon.getPointN(0), 30, 10);
        assertPoint(polygon.getPointN(1), 40, 40);
        assertPoint(polygon.getPointN(2), 20, 40);
        assertPoint(polygon.getPointN(3), 10, 20);
        assertPoint(polygon.getPointN(4), 30, 10);
    }

    @Test
    public void polygon_with_hole() {
        Polygon polygon = (Polygon) Spatial.parseWKT("POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10),\n" +
                "(20 30, 35 35, 30 20, 20 30))");
        assertThat(polygon.getNumInteriorRing()).isEqualTo(1);
        assertPoint(polygon.getPointN(0), 35, 10);
        assertPoint(polygon.getPointN(1), 45, 45);
        assertPoint(polygon.getPointN(2), 15, 40);
        assertPoint(polygon.getPointN(3), 10, 20);
        assertPoint(polygon.getPointN(4), 35, 10);

        assertPoint(polygon.getPointN(5), 20, 30);
        assertPoint(polygon.getPointN(6), 35, 35);
        assertPoint(polygon.getPointN(7), 30, 20);
        assertPoint(polygon.getPointN(8), 20, 30);
    }

    @Test
    public void multipoint1() {
        MultiPoint polygon = (MultiPoint) Spatial.parseWKT("MULTIPOINT ((10 40), (40 30), (20 20), (30 10))");
        assertPoint(polygon.getPointN(0), 10, 40);
        assertPoint(polygon.getPointN(1), 40, 30);
        assertPoint(polygon.getPointN(2), 20, 20);
        assertPoint(polygon.getPointN(3), 30, 10);
    }

    @Test
    public void multipoint2() {
        MultiPoint polygon = (MultiPoint) Spatial.parseWKT("MULTIPOINT (10 40, 40 30, 20 20, 30 10)");
        assertPoint(polygon.getPointN(0), 10, 40);
        assertPoint(polygon.getPointN(1), 40, 30);
        assertPoint(polygon.getPointN(2), 20, 20);
        assertPoint(polygon.getPointN(3), 30, 10);
    }

    @Test
    public void multilinestring() {
        MultiLineString multiLineString = (MultiLineString) Spatial.parseWKT("MULTILINESTRING ((35 10, 45 45, 15 40, 10 20, 35 10),\n" +
                "(20 30, 35 35, 30 20, 20 30))");
        assertPoint(multiLineString.getPointN(0), 35, 10);
        assertPoint(multiLineString.getPointN(1), 45, 45);
        assertPoint(multiLineString.getPointN(2), 15, 40);
        assertPoint(multiLineString.getPointN(3), 10, 20);
        assertPoint(multiLineString.getPointN(4), 35, 10);

        assertPoint(multiLineString.getPointN(5), 20, 30);
        assertPoint(multiLineString.getPointN(6), 35, 35);
        assertPoint(multiLineString.getPointN(7), 30, 20);
        assertPoint(multiLineString.getPointN(8), 20, 30);
    }

    @Test
    public void multipolygon() {
        MultiPolygon multiPolygon = (MultiPolygon) Spatial.parseWKT(
                "MULTIPOLYGON (((35 10, 45 45, 15 40, 10 20, 35 10)),\n" +
                "((20 30, 35 35, 30 20, 20 30)))");
        assertThat(multiPolygon.getNumGeometries()).isEqualTo(2);
        assertPoint(multiPolygon.getPointN(0), 35, 10);
        assertPoint(multiPolygon.getPointN(1), 45, 45);
        assertPoint(multiPolygon.getPointN(2), 15, 40);
        assertPoint(multiPolygon.getPointN(3), 10, 20);
        assertPoint(multiPolygon.getPointN(4), 35, 10);

        assertPoint(multiPolygon.getPointN(5), 20, 30);
        assertPoint(multiPolygon.getPointN(6), 35, 35);
        assertPoint(multiPolygon.getPointN(7), 30, 20);
        assertPoint(multiPolygon.getPointN(8), 20, 30);
    }

    @Test
    public void multipolygon_with_hole() {
        MultiPolygon multiPolygon = (MultiPolygon) Spatial.parseWKT(
                "MULTIPOLYGON (((40 40, 20 45, 45 30, 40 40)),\n" +
                        "((20 35, 10 30, 10 10, 30 5, 45 20, 20 35),\n" +
                        "(30 20, 20 15, 20 25, 30 20)))");
        assertThat(multiPolygon.getNumGeometries()).isEqualTo(2);
        assertPoint(multiPolygon.getPointN(0), 40, 40);
        assertPoint(multiPolygon.getPointN(1), 20, 45);
        assertPoint(multiPolygon.getPointN(2), 45, 30);
        assertPoint(multiPolygon.getPointN(3), 40, 40);

        assertPoint(multiPolygon.getPointN(4), 20, 35);
        assertPoint(multiPolygon.getPointN(5), 10, 30);
        assertPoint(multiPolygon.getPointN(6), 10, 10);
        assertPoint(multiPolygon.getPointN(7), 30, 5);
        assertPoint(multiPolygon.getPointN(8), 45, 20);
        assertPoint(multiPolygon.getPointN(9), 20, 35);

        assertPoint(multiPolygon.getPointN(10), 30, 20);
        assertPoint(multiPolygon.getPointN(11), 20, 15);
        assertPoint(multiPolygon.getPointN(12), 20, 25);
        assertPoint(multiPolygon.getPointN(13), 30, 20);
    }

    private void assertPoint(Point point, double x, double y) {
        assertThat(point.getX()).isEqualTo(x);
        assertThat(point.getY()).isEqualTo(y);
    }

}
