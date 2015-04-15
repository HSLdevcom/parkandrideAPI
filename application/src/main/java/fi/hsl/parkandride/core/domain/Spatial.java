// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.Double.parseDouble;
import static org.geolatte.geom.DimensionalFlag.d2D;
import static org.geolatte.geom.PointSequenceBuilders.variableSized;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.*;
import org.geolatte.geom.*;
import org.geolatte.geom.codec.Wkt;
import org.geolatte.geom.crs.CrsId;

import com.google.common.collect.Lists;

import fi.hsl.parkandride.core.domain.wkt.WKTBaseVisitor;
import fi.hsl.parkandride.core.domain.wkt.WKTLexer;
import fi.hsl.parkandride.core.domain.wkt.WKTParser;

public class Spatial {

    public static final CrsId WGS84 = CrsId.valueOf(4326);

    private static final ANTLRErrorListener ERROR_LISTENER = new BaseErrorListener() {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer,
                                Object offendingSymbol,
                                int line,
                                int charPositionInLine,
                                String msg,
                                RecognitionException e) {
            throw new IllegalArgumentException("Line " + line + ":" + charPositionInLine + " " + msg);
        }
    };

    public static Geometry fromWkt(String wkt) {
        return parseWKT(wkt);
    }

    public static String toWkt(Geometry geometry) {
        return Wkt.newEncoder(Wkt.Dialect.POSTGIS_EWKT_1).encode(geometry);
    }

    public static Polygon fromWktPolygon(String wkt) {
        return (Polygon) parseWKT(wkt);
    }

    public static Geometry parseWKT(String wkt) {
        if (isNullOrEmpty(wkt)) {
            return null;
        }
        try {
            return newParser(wkt).geometry().accept(WKT_VISITOR).toGeometry();
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(
                    "Expected a valid WKT Point, LineString, Polygon, MultiPoint, MultiLineString or MultiPolygon. " + e.getMessage(), e);
        }
    }

    private static WKTParser newParser(String input) {
        WKTLexer lexer = new WKTLexer(new ANTLRInputStream(input));
        lexer.removeErrorListeners();
        lexer.addErrorListener(ERROR_LISTENER);
        WKTParser parser = new WKTParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(ERROR_LISTENER);
        return parser;
    }

    private static final WKTBaseVisitor<Builder> WKT_VISITOR = new WKTBaseVisitor<Builder>() {

        @Override
        public Builder visitPointGeometry(WKTParser.PointGeometryContext ctx) {
            return new GeometryWrapper(((Points) visitChildren(ctx)).toPoint());
        }

        @Override
        public Builder visitLineStringGeometry(WKTParser.LineStringGeometryContext ctx) {
            return new GeometryWrapper(((Lines) visitChildren(ctx)).toLineString());
        }

        @Override
        public Builder visitPolygonGeometry(WKTParser.PolygonGeometryContext ctx) {
            return new GeometryWrapper(((Shapes) visitChildren(ctx)).toPolygon());
        }

        @Override
        public Builder visitMultiPointGeometry(WKTParser.MultiPointGeometryContext ctx) {
            return new GeometryWrapper(((Points) visitChildren(ctx)).toMultiPoint());
        }

        @Override
        public Builder visitMultiLineStringGeometry(WKTParser.MultiLineStringGeometryContext ctx) {
            return new GeometryWrapper(((Lines) visitChildren(ctx)).toMultiLineString());
        }

        @Override
        public Builder visitMultiPolygonGeometry(WKTParser.MultiPolygonGeometryContext ctx) {
            return new GeometryWrapper(((Shapes) visitChildren(ctx)).toMultiPolygon());
        }

        @Override
        public Builder visitPolygon(WKTParser.PolygonContext ctx) {
            Lines lines = (Lines) visitChildren(ctx);
            return new Shapes(lines);
        }

        @Override
        public Builder visitLineString(WKTParser.LineStringContext ctx) {
            Points points = (Points) visitChildren(ctx);
            return new Lines(points);
        }

        @Override
        public Builder visitPoint(WKTParser.PointContext ctx) {
            return new Points(parseDouble(ctx.x.getText()), parseDouble(ctx.y.getText()));
        }

        @Override
        protected Builder aggregateResult(Builder aggregate, Builder nextResult) {
            return aggregate != null ? aggregate.append(nextResult) : nextResult;
        }

    };

    private abstract static class Builder {
        Builder append(Builder builder) {
            throw new UnsupportedOperationException();
        }

        Geometry toGeometry() {
            throw new UnsupportedOperationException();
        }
    }

    private static class GeometryWrapper extends Builder {
        private final Geometry geometry;

        private GeometryWrapper(Geometry geometry) {
            this.geometry = geometry;
        }

        @Override
        Geometry toGeometry() {
            return geometry;
        }
    }

    private static class Points extends Builder {

        final PointSequenceBuilder points = variableSized(d2D, WGS84);

        public Points(double x, double y) {
            points.add(x, y);
        }

        @Override
        Builder append(Builder builder) {
            if (builder != null) {
                Points other = (Points) builder;
                PointSequence pointSequence = other.points.toPointSequence();
                for (int i = 0; i < pointSequence.size(); i++) {
                    points.add(pointSequence.getX(i), pointSequence.getY(i));
                }
            }
            return this;
        }

        Point toPoint() {
            return new Point(points.toPointSequence());
        }

        LineString toLineString() {
            return new LineString(points.toPointSequence());
        }

        LinearRing toLinearRing() {
            return new LinearRing(points.toPointSequence());
        }

        MultiPoint toMultiPoint() {
            return new MultiPoint(toPointArray());
        }

        Point[] toPointArray() {
            PointSequence pointSequence = points.toPointSequence();
            Point[] pointArray = new Point[pointSequence.size()];
            int i=0;
            for (Point point : pointSequence) {
                pointArray[i++] = point;
            }
            return pointArray;
        }
    }

    private static class Lines extends Builder {

        private final List<Points> lines = new ArrayList<>();

        Lines(Points points) {
            lines.add(points);
        }

        @Override
        Builder append(Builder builder) {
            if (builder != null) {
                lines.addAll(((Lines) builder).lines);
            }
            return this;
        }

        Polygon toPolygon() {
            return new Polygon(toLinearRings());
        }

        LinearRing[] toLinearRings() {
            List<LinearRing> rings = Lists.transform(lines, Points::toLinearRing);
            return rings.toArray(rings.toArray(new LinearRing[rings.size()]));
        }

        Geometry toMultiLineString() {
            return new MultiLineString(toLineStrings());
        }

        LineString[] toLineStrings() {
            List<LineString> lineStrings = Lists.transform(lines, Points::toLineString);
            return lineStrings.toArray(new LineString[lineStrings.size()]);
        }

        LineString toLineString() {
            return lines.get(0).toLineString();
        }
    }

    private static class Shapes extends Builder {

        private final List<Lines> shapes = new ArrayList<>();

        Shapes(Lines lines) {
            shapes.add(lines);
        }

        @Override
        Builder append(Builder builder) {
            if (builder != null) {
                shapes.addAll(((Shapes) builder).shapes);
            }
            return this;
        }

        Polygon toPolygon() {
            return shapes.get(0).toPolygon();
        }

        Geometry toMultiPolygon() {
            return new MultiPolygon(toPolygons());
        }

        Polygon[] toPolygons() {
            List<Polygon> polygons = Lists.transform(shapes, Lines::toPolygon);
            return polygons.toArray(new Polygon[polygons.size()]);
        }
    }


}
