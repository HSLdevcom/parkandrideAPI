// Generated from /Users/samppa/git/parkandrideAPI/application/src/main/antlr/WKT.g4 by ANTLR 4.5
package fi.hsl.parkandride.core.domain.wkt;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link WKTParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface WKTVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link WKTParser#geometry}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGeometry(@NotNull WKTParser.GeometryContext ctx);
	/**
	 * Visit a parse tree produced by {@link WKTParser#pointGeometry}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPointGeometry(@NotNull WKTParser.PointGeometryContext ctx);
	/**
	 * Visit a parse tree produced by {@link WKTParser#lineStringGeometry}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLineStringGeometry(@NotNull WKTParser.LineStringGeometryContext ctx);
	/**
	 * Visit a parse tree produced by {@link WKTParser#polygonGeometry}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPolygonGeometry(@NotNull WKTParser.PolygonGeometryContext ctx);
	/**
	 * Visit a parse tree produced by {@link WKTParser#multiPointGeometry}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiPointGeometry(@NotNull WKTParser.MultiPointGeometryContext ctx);
	/**
	 * Visit a parse tree produced by {@link WKTParser#multiLineStringGeometry}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiLineStringGeometry(@NotNull WKTParser.MultiLineStringGeometryContext ctx);
	/**
	 * Visit a parse tree produced by {@link WKTParser#multiPolygonGeometry}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiPolygonGeometry(@NotNull WKTParser.MultiPolygonGeometryContext ctx);
	/**
	 * Visit a parse tree produced by {@link WKTParser#pointOrClosedPoint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPointOrClosedPoint(@NotNull WKTParser.PointOrClosedPointContext ctx);
	/**
	 * Visit a parse tree produced by {@link WKTParser#polygon}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPolygon(@NotNull WKTParser.PolygonContext ctx);
	/**
	 * Visit a parse tree produced by {@link WKTParser#lineString}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLineString(@NotNull WKTParser.LineStringContext ctx);
	/**
	 * Visit a parse tree produced by {@link WKTParser#point}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPoint(@NotNull WKTParser.PointContext ctx);
}