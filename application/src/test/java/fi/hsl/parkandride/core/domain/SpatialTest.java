// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import static fi.hsl.parkandride.core.domain.Spatial.fromWktPolygon;
import static fi.hsl.parkandride.core.domain.Spatial.parseWKT;
import static org.assertj.core.api.Assertions.assertThat;

import org.geolatte.geom.*;
import org.junit.Test;


public class SpatialTest {

    @Test(expected = IllegalArgumentException.class)
    public void missing_inner_parenthesis() {
        fromWktPolygon("POLYGON( 30 10, 40 40, 20 40, 10 20, 30 10 )");
    }

    @Test(expected = IllegalArgumentException.class)
    public void missing_ending_parenthesis() {
        parseWKT("POINT(1 1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void extra_input_after_valid_geometry() {
        parseWKT("POINT(1 1) x");
    }

    @Test(expected = IllegalArgumentException.class)
    public void valid_tokens_but_invalid_parser_syntax() {
        parseWKT("POINT POLYGON");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalid_type() {
        parseWKT("PINT(1 1)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void unclosed_polygon() {
        parseWKT("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 11))");
    }

    @Test(expected = IllegalArgumentException.class)
    public void too_short_polygon() {
        parseWKT("POLYGON((1 1, 2 2, 1 1))");
    }

    @Test
    public void ignore_whitespace() {
        assertPoint((Point) parseWKT("\nPOINT\t ( 1\r2)\t\r \n"), 1, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whitespace_not_allowed_within_type() {
        parseWKT("POIN T( 1 2)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegal_nested_multilinestring() {
        parseWKT("multilinestring((1 1, 2 2), 3 3)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegal_double_zero() {
        parseWKT("POINT(0 00)");
    }

    @Test
    public void null_wkt_is_null_geometry() {
        assertThat(parseWKT(null)).isNull();
    }

    @Test
    public void empty_wkt_is_null_geometry() {
        assertThat(parseWKT("")).isNull();
    }

    @Test
    public void lower_case_geometries() {
        assertThat(parseWKT("point(1 1)")).isInstanceOf(Point.class);
        assertThat(parseWKT("linestring(1 1, 2 2)")).isInstanceOf(LineString.class);
        assertThat(parseWKT("polygon((1 1, 2 2, 3 3, 1 1))")).isInstanceOf(Polygon.class);
        assertThat(parseWKT("multipoint(1 1, 2 2, 3 3)")).isInstanceOf(MultiPoint.class);
        assertThat(parseWKT("multilinestring((1 1, 2 2, 3 3), (4 4, 5 5, 6 6))")).isInstanceOf(MultiLineString.class);
        assertThat(parseWKT("multipolygon(((1 1, 2 2, 3 3, 1 1)))")).isInstanceOf(MultiPolygon.class);
    }

    @Test
    public void camel_case_geometries() {
        assertThat(parseWKT("Point(1 1)")).isInstanceOf(Point.class);
        assertThat(parseWKT("LineString(1 1, 2 2)")).isInstanceOf(LineString.class);
        assertThat(parseWKT("Polygon((1 1, 2 2, 3 3, 1 1))")).isInstanceOf(Polygon.class);
        assertThat(parseWKT("MultiPoint(1 1, 2 2, 3 3)")).isInstanceOf(MultiPoint.class);
        assertThat(parseWKT("MultiLineString((1 1, 2 2, 3 3), (4 4, 5 5, 6 6))")).isInstanceOf(MultiLineString.class);
        assertThat(parseWKT("MultiPolygon(((1 1, 2 2, 3 3, 1 1)))")).isInstanceOf(MultiPolygon.class);
    }

    @Test
    public void point() {
        assertPoint((Point) parseWKT("POINT(0.0123 123)"), 0.0123, 123);
    }

    @Test
    public void negative_coordinates() {
        assertPoint((Point) parseWKT("POINT(-0.1 -123)"), -0.1, -123);
    }

    @Test
    public void lineString() {
        LineString ls = (LineString) parseWKT("\tLINESTRING (30.0 10.10, 10 30, 40 40)\n");
        assertPoint(ls.getPointN(0), 30, 10.1);
        assertPoint(ls.getPointN(1), 10, 30);
        assertPoint(ls.getPointN(2), 40, 40);
    }

    @Test
    public void polygon() {
        Polygon polygon = (Polygon) parseWKT("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))");
        assertThat(polygon.getNumInteriorRing()).isEqualTo(0);
        assertPoint(polygon.getPointN(0), 30, 10);
        assertPoint(polygon.getPointN(1), 40, 40);
        assertPoint(polygon.getPointN(2), 20, 40);
        assertPoint(polygon.getPointN(3), 10, 20);
        assertPoint(polygon.getPointN(4), 30, 10);
    }

    @Test
    public void polygon_with_hole() {
        Polygon polygon = (Polygon) parseWKT("POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10),\n" +
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
        MultiPoint polygon = (MultiPoint) parseWKT("MULTIPOINT ((10 40), (40 30), (20 20), (30 10))");
        assertPoint(polygon.getPointN(0), 10, 40);
        assertPoint(polygon.getPointN(1), 40, 30);
        assertPoint(polygon.getPointN(2), 20, 20);
        assertPoint(polygon.getPointN(3), 30, 10);
    }

    @Test
    public void multipoint2() {
        MultiPoint polygon = (MultiPoint) parseWKT("MULTIPOINT (10 40, 40 30, 20 20, 30 10)");
        assertPoint(polygon.getPointN(0), 10, 40);
        assertPoint(polygon.getPointN(1), 40, 30);
        assertPoint(polygon.getPointN(2), 20, 20);
        assertPoint(polygon.getPointN(3), 30, 10);
    }

    @Test
    public void multilinestring() {
        MultiLineString multiLineString = (MultiLineString) parseWKT("MULTILINESTRING ((35 10, 45 45, 15 40, 10 20, 35 10),\n" +
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
        MultiPolygon multiPolygon = (MultiPolygon) parseWKT(
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
        MultiPolygon multiPolygon = (MultiPolygon) parseWKT(
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
