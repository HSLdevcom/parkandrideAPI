// Generated from /Users/samppa/git/parkandrideAPI/application/src/main/antlr/WKT.g4 by ANTLR 4.5
package fi.hsl.parkandride.core.domain.wkt;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link WKTParser}.
 */
public interface WKTListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link WKTParser#geometry}.
	 * @param ctx the parse tree
	 */
	void enterGeometry(@NotNull WKTParser.GeometryContext ctx);
	/**
	 * Exit a parse tree produced by {@link WKTParser#geometry}.
	 * @param ctx the parse tree
	 */
	void exitGeometry(@NotNull WKTParser.GeometryContext ctx);
	/**
	 * Enter a parse tree produced by {@link WKTParser#pointGeometry}.
	 * @param ctx the parse tree
	 */
	void enterPointGeometry(@NotNull WKTParser.PointGeometryContext ctx);
	/**
	 * Exit a parse tree produced by {@link WKTParser#pointGeometry}.
	 * @param ctx the parse tree
	 */
	void exitPointGeometry(@NotNull WKTParser.PointGeometryContext ctx);
	/**
	 * Enter a parse tree produced by {@link WKTParser#lineStringGeometry}.
	 * @param ctx the parse tree
	 */
	void enterLineStringGeometry(@NotNull WKTParser.LineStringGeometryContext ctx);
	/**
	 * Exit a parse tree produced by {@link WKTParser#lineStringGeometry}.
	 * @param ctx the parse tree
	 */
	void exitLineStringGeometry(@NotNull WKTParser.LineStringGeometryContext ctx);
	/**
	 * Enter a parse tree produced by {@link WKTParser#polygonGeometry}.
	 * @param ctx the parse tree
	 */
	void enterPolygonGeometry(@NotNull WKTParser.PolygonGeometryContext ctx);
	/**
	 * Exit a parse tree produced by {@link WKTParser#polygonGeometry}.
	 * @param ctx the parse tree
	 */
	void exitPolygonGeometry(@NotNull WKTParser.PolygonGeometryContext ctx);
	/**
	 * Enter a parse tree produced by {@link WKTParser#multiPointGeometry}.
	 * @param ctx the parse tree
	 */
	void enterMultiPointGeometry(@NotNull WKTParser.MultiPointGeometryContext ctx);
	/**
	 * Exit a parse tree produced by {@link WKTParser#multiPointGeometry}.
	 * @param ctx the parse tree
	 */
	void exitMultiPointGeometry(@NotNull WKTParser.MultiPointGeometryContext ctx);
	/**
	 * Enter a parse tree produced by {@link WKTParser#multiLineStringGeometry}.
	 * @param ctx the parse tree
	 */
	void enterMultiLineStringGeometry(@NotNull WKTParser.MultiLineStringGeometryContext ctx);
	/**
	 * Exit a parse tree produced by {@link WKTParser#multiLineStringGeometry}.
	 * @param ctx the parse tree
	 */
	void exitMultiLineStringGeometry(@NotNull WKTParser.MultiLineStringGeometryContext ctx);
	/**
	 * Enter a parse tree produced by {@link WKTParser#multiPolygonGeometry}.
	 * @param ctx the parse tree
	 */
	void enterMultiPolygonGeometry(@NotNull WKTParser.MultiPolygonGeometryContext ctx);
	/**
	 * Exit a parse tree produced by {@link WKTParser#multiPolygonGeometry}.
	 * @param ctx the parse tree
	 */
	void exitMultiPolygonGeometry(@NotNull WKTParser.MultiPolygonGeometryContext ctx);
	/**
	 * Enter a parse tree produced by {@link WKTParser#pointOrClosedPoint}.
	 * @param ctx the parse tree
	 */
	void enterPointOrClosedPoint(@NotNull WKTParser.PointOrClosedPointContext ctx);
	/**
	 * Exit a parse tree produced by {@link WKTParser#pointOrClosedPoint}.
	 * @param ctx the parse tree
	 */
	void exitPointOrClosedPoint(@NotNull WKTParser.PointOrClosedPointContext ctx);
	/**
	 * Enter a parse tree produced by {@link WKTParser#polygon}.
	 * @param ctx the parse tree
	 */
	void enterPolygon(@NotNull WKTParser.PolygonContext ctx);
	/**
	 * Exit a parse tree produced by {@link WKTParser#polygon}.
	 * @param ctx the parse tree
	 */
	void exitPolygon(@NotNull WKTParser.PolygonContext ctx);
	/**
	 * Enter a parse tree produced by {@link WKTParser#lineString}.
	 * @param ctx the parse tree
	 */
	void enterLineString(@NotNull WKTParser.LineStringContext ctx);
	/**
	 * Exit a parse tree produced by {@link WKTParser#lineString}.
	 * @param ctx the parse tree
	 */
	void exitLineString(@NotNull WKTParser.LineStringContext ctx);
	/**
	 * Enter a parse tree produced by {@link WKTParser#point}.
	 * @param ctx the parse tree
	 */
	void enterPoint(@NotNull WKTParser.PointContext ctx);
	/**
	 * Exit a parse tree produced by {@link WKTParser#point}.
	 * @param ctx the parse tree
	 */
	void exitPoint(@NotNull WKTParser.PointContext ctx);
}