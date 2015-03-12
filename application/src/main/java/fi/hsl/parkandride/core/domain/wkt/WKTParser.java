// Generated from /Users/samppa/git/parkandrideAPI/application/src/main/antlr/WKT.g4 by ANTLR 4.5
package fi.hsl.parkandride.core.domain.wkt;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class WKTParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, Decimal=4, IntegerPart=5, DecimalPart=6, POINT=7, 
		LINESTRING=8, POLYGON=9, MULTIPOINT=10, MULTILINESTRING=11, MULTIPOLYGON=12, 
		WS=13;
	public static final int
		RULE_geometry = 0, RULE_pointGeometry = 1, RULE_lineStringGeometry = 2, 
		RULE_polygonGeometry = 3, RULE_multiPointGeometry = 4, RULE_multiLineStringGeometry = 5, 
		RULE_multiPolygonGeometry = 6, RULE_pointOrClosedPoint = 7, RULE_polygon = 8, 
		RULE_lineString = 9, RULE_point = 10;
	public static final String[] ruleNames = {
		"geometry", "pointGeometry", "lineStringGeometry", "polygonGeometry", 
		"multiPointGeometry", "multiLineStringGeometry", "multiPolygonGeometry", 
		"pointOrClosedPoint", "polygon", "lineString", "point"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "')'", "','"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, "Decimal", "IntegerPart", "DecimalPart", "POINT", 
		"LINESTRING", "POLYGON", "MULTIPOINT", "MULTILINESTRING", "MULTIPOLYGON", 
		"WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override
	@NotNull
	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "WKT.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public WKTParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class GeometryContext extends ParserRuleContext {
		public PolygonGeometryContext polygonGeometry() {
			return getRuleContext(PolygonGeometryContext.class,0);
		}
		public LineStringGeometryContext lineStringGeometry() {
			return getRuleContext(LineStringGeometryContext.class,0);
		}
		public PointGeometryContext pointGeometry() {
			return getRuleContext(PointGeometryContext.class,0);
		}
		public MultiPointGeometryContext multiPointGeometry() {
			return getRuleContext(MultiPointGeometryContext.class,0);
		}
		public MultiLineStringGeometryContext multiLineStringGeometry() {
			return getRuleContext(MultiLineStringGeometryContext.class,0);
		}
		public MultiPolygonGeometryContext multiPolygonGeometry() {
			return getRuleContext(MultiPolygonGeometryContext.class,0);
		}
		public GeometryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_geometry; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).enterGeometry(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).exitGeometry(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WKTVisitor ) return ((WKTVisitor<? extends T>)visitor).visitGeometry(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GeometryContext geometry() throws RecognitionException {
		GeometryContext _localctx = new GeometryContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_geometry);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(28);
			switch (_input.LA(1)) {
			case POLYGON:
				{
				setState(22); 
				polygonGeometry();
				}
				break;
			case LINESTRING:
				{
				setState(23); 
				lineStringGeometry();
				}
				break;
			case POINT:
				{
				setState(24); 
				pointGeometry();
				}
				break;
			case MULTIPOINT:
				{
				setState(25); 
				multiPointGeometry();
				}
				break;
			case MULTILINESTRING:
				{
				setState(26); 
				multiLineStringGeometry();
				}
				break;
			case MULTIPOLYGON:
				{
				setState(27); 
				multiPolygonGeometry();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PointGeometryContext extends ParserRuleContext {
		public TerminalNode POINT() { return getToken(WKTParser.POINT, 0); }
		public PointContext point() {
			return getRuleContext(PointContext.class,0);
		}
		public TerminalNode EOF() { return getToken(WKTParser.EOF, 0); }
		public PointGeometryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pointGeometry; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).enterPointGeometry(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).exitPointGeometry(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WKTVisitor ) return ((WKTVisitor<? extends T>)visitor).visitPointGeometry(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PointGeometryContext pointGeometry() throws RecognitionException {
		PointGeometryContext _localctx = new PointGeometryContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_pointGeometry);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(30); 
			match(POINT);
			setState(31); 
			match(T__0);
			setState(32); 
			point();
			setState(33); 
			match(T__1);
			setState(34); 
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LineStringGeometryContext extends ParserRuleContext {
		public TerminalNode LINESTRING() { return getToken(WKTParser.LINESTRING, 0); }
		public LineStringContext lineString() {
			return getRuleContext(LineStringContext.class,0);
		}
		public TerminalNode EOF() { return getToken(WKTParser.EOF, 0); }
		public LineStringGeometryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lineStringGeometry; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).enterLineStringGeometry(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).exitLineStringGeometry(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WKTVisitor ) return ((WKTVisitor<? extends T>)visitor).visitLineStringGeometry(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LineStringGeometryContext lineStringGeometry() throws RecognitionException {
		LineStringGeometryContext _localctx = new LineStringGeometryContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_lineStringGeometry);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36); 
			match(LINESTRING);
			setState(37); 
			lineString();
			setState(38); 
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PolygonGeometryContext extends ParserRuleContext {
		public TerminalNode POLYGON() { return getToken(WKTParser.POLYGON, 0); }
		public PolygonContext polygon() {
			return getRuleContext(PolygonContext.class,0);
		}
		public TerminalNode EOF() { return getToken(WKTParser.EOF, 0); }
		public PolygonGeometryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_polygonGeometry; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).enterPolygonGeometry(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).exitPolygonGeometry(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WKTVisitor ) return ((WKTVisitor<? extends T>)visitor).visitPolygonGeometry(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PolygonGeometryContext polygonGeometry() throws RecognitionException {
		PolygonGeometryContext _localctx = new PolygonGeometryContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_polygonGeometry);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40); 
			match(POLYGON);
			setState(41); 
			polygon();
			setState(42); 
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MultiPointGeometryContext extends ParserRuleContext {
		public TerminalNode MULTIPOINT() { return getToken(WKTParser.MULTIPOINT, 0); }
		public List<PointOrClosedPointContext> pointOrClosedPoint() {
			return getRuleContexts(PointOrClosedPointContext.class);
		}
		public PointOrClosedPointContext pointOrClosedPoint(int i) {
			return getRuleContext(PointOrClosedPointContext.class,i);
		}
		public TerminalNode EOF() { return getToken(WKTParser.EOF, 0); }
		public MultiPointGeometryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiPointGeometry; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).enterMultiPointGeometry(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).exitMultiPointGeometry(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WKTVisitor ) return ((WKTVisitor<? extends T>)visitor).visitMultiPointGeometry(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultiPointGeometryContext multiPointGeometry() throws RecognitionException {
		MultiPointGeometryContext _localctx = new MultiPointGeometryContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_multiPointGeometry);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44); 
			match(MULTIPOINT);
			setState(45); 
			match(T__0);
			setState(46); 
			pointOrClosedPoint();
			setState(51);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(47); 
				match(T__2);
				setState(48); 
				pointOrClosedPoint();
				}
				}
				setState(53);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(54); 
			match(T__1);
			setState(55); 
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MultiLineStringGeometryContext extends ParserRuleContext {
		public TerminalNode MULTILINESTRING() { return getToken(WKTParser.MULTILINESTRING, 0); }
		public List<LineStringContext> lineString() {
			return getRuleContexts(LineStringContext.class);
		}
		public LineStringContext lineString(int i) {
			return getRuleContext(LineStringContext.class,i);
		}
		public TerminalNode EOF() { return getToken(WKTParser.EOF, 0); }
		public MultiLineStringGeometryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiLineStringGeometry; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).enterMultiLineStringGeometry(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).exitMultiLineStringGeometry(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WKTVisitor ) return ((WKTVisitor<? extends T>)visitor).visitMultiLineStringGeometry(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultiLineStringGeometryContext multiLineStringGeometry() throws RecognitionException {
		MultiLineStringGeometryContext _localctx = new MultiLineStringGeometryContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_multiLineStringGeometry);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(57); 
			match(MULTILINESTRING);
			setState(58); 
			match(T__0);
			setState(59); 
			lineString();
			setState(64);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(60); 
				match(T__2);
				setState(61); 
				lineString();
				}
				}
				setState(66);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(67); 
			match(T__1);
			setState(68); 
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MultiPolygonGeometryContext extends ParserRuleContext {
		public TerminalNode MULTIPOLYGON() { return getToken(WKTParser.MULTIPOLYGON, 0); }
		public List<PolygonContext> polygon() {
			return getRuleContexts(PolygonContext.class);
		}
		public PolygonContext polygon(int i) {
			return getRuleContext(PolygonContext.class,i);
		}
		public TerminalNode EOF() { return getToken(WKTParser.EOF, 0); }
		public MultiPolygonGeometryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiPolygonGeometry; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).enterMultiPolygonGeometry(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).exitMultiPolygonGeometry(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WKTVisitor ) return ((WKTVisitor<? extends T>)visitor).visitMultiPolygonGeometry(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultiPolygonGeometryContext multiPolygonGeometry() throws RecognitionException {
		MultiPolygonGeometryContext _localctx = new MultiPolygonGeometryContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_multiPolygonGeometry);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70); 
			match(MULTIPOLYGON);
			setState(71); 
			match(T__0);
			setState(72); 
			polygon();
			setState(77);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(73); 
				match(T__2);
				setState(74); 
				polygon();
				}
				}
				setState(79);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(80); 
			match(T__1);
			setState(81); 
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PointOrClosedPointContext extends ParserRuleContext {
		public PointContext point() {
			return getRuleContext(PointContext.class,0);
		}
		public PointOrClosedPointContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pointOrClosedPoint; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).enterPointOrClosedPoint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).exitPointOrClosedPoint(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WKTVisitor ) return ((WKTVisitor<? extends T>)visitor).visitPointOrClosedPoint(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PointOrClosedPointContext pointOrClosedPoint() throws RecognitionException {
		PointOrClosedPointContext _localctx = new PointOrClosedPointContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_pointOrClosedPoint);
		try {
			setState(88);
			switch (_input.LA(1)) {
			case Decimal:
				enterOuterAlt(_localctx, 1);
				{
				setState(83); 
				point();
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 2);
				{
				setState(84); 
				match(T__0);
				setState(85); 
				point();
				setState(86); 
				match(T__1);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PolygonContext extends ParserRuleContext {
		public List<LineStringContext> lineString() {
			return getRuleContexts(LineStringContext.class);
		}
		public LineStringContext lineString(int i) {
			return getRuleContext(LineStringContext.class,i);
		}
		public PolygonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_polygon; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).enterPolygon(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).exitPolygon(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WKTVisitor ) return ((WKTVisitor<? extends T>)visitor).visitPolygon(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PolygonContext polygon() throws RecognitionException {
		PolygonContext _localctx = new PolygonContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_polygon);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90); 
			match(T__0);
			setState(91); 
			lineString();
			setState(96);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(92); 
				match(T__2);
				setState(93); 
				lineString();
				}
				}
				setState(98);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(99); 
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LineStringContext extends ParserRuleContext {
		public List<PointContext> point() {
			return getRuleContexts(PointContext.class);
		}
		public PointContext point(int i) {
			return getRuleContext(PointContext.class,i);
		}
		public LineStringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lineString; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).enterLineString(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).exitLineString(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WKTVisitor ) return ((WKTVisitor<? extends T>)visitor).visitLineString(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LineStringContext lineString() throws RecognitionException {
		LineStringContext _localctx = new LineStringContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_lineString);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(101); 
			match(T__0);
			setState(102); 
			point();
			setState(107);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(103); 
				match(T__2);
				setState(104); 
				point();
				}
				}
				setState(109);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(110); 
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PointContext extends ParserRuleContext {
		public List<TerminalNode> Decimal() { return getTokens(WKTParser.Decimal); }
		public TerminalNode Decimal(int i) {
			return getToken(WKTParser.Decimal, i);
		}
		public PointContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_point; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).enterPoint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WKTListener ) ((WKTListener)listener).exitPoint(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WKTVisitor ) return ((WKTVisitor<? extends T>)visitor).visitPoint(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PointContext point() throws RecognitionException {
		PointContext _localctx = new PointContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_point);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(112); 
			match(Decimal);
			setState(113); 
			match(Decimal);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\17v\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4"+
		"\f\t\f\3\2\3\2\3\2\3\2\3\2\3\2\5\2\37\n\2\3\3\3\3\3\3\3\3\3\3\3\3\3\4"+
		"\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\7\6\64\n\6\f\6\16\6\67"+
		"\13\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\7\7A\n\7\f\7\16\7D\13\7\3\7\3\7"+
		"\3\7\3\b\3\b\3\b\3\b\3\b\7\bN\n\b\f\b\16\bQ\13\b\3\b\3\b\3\b\3\t\3\t\3"+
		"\t\3\t\3\t\5\t[\n\t\3\n\3\n\3\n\3\n\7\na\n\n\f\n\16\nd\13\n\3\n\3\n\3"+
		"\13\3\13\3\13\3\13\7\13l\n\13\f\13\16\13o\13\13\3\13\3\13\3\f\3\f\3\f"+
		"\3\f\2\2\r\2\4\6\b\n\f\16\20\22\24\26\2\2u\2\36\3\2\2\2\4 \3\2\2\2\6&"+
		"\3\2\2\2\b*\3\2\2\2\n.\3\2\2\2\f;\3\2\2\2\16H\3\2\2\2\20Z\3\2\2\2\22\\"+
		"\3\2\2\2\24g\3\2\2\2\26r\3\2\2\2\30\37\5\b\5\2\31\37\5\6\4\2\32\37\5\4"+
		"\3\2\33\37\5\n\6\2\34\37\5\f\7\2\35\37\5\16\b\2\36\30\3\2\2\2\36\31\3"+
		"\2\2\2\36\32\3\2\2\2\36\33\3\2\2\2\36\34\3\2\2\2\36\35\3\2\2\2\37\3\3"+
		"\2\2\2 !\7\t\2\2!\"\7\3\2\2\"#\5\26\f\2#$\7\4\2\2$%\7\2\2\3%\5\3\2\2\2"+
		"&\'\7\n\2\2\'(\5\24\13\2()\7\2\2\3)\7\3\2\2\2*+\7\13\2\2+,\5\22\n\2,-"+
		"\7\2\2\3-\t\3\2\2\2./\7\f\2\2/\60\7\3\2\2\60\65\5\20\t\2\61\62\7\5\2\2"+
		"\62\64\5\20\t\2\63\61\3\2\2\2\64\67\3\2\2\2\65\63\3\2\2\2\65\66\3\2\2"+
		"\2\668\3\2\2\2\67\65\3\2\2\289\7\4\2\29:\7\2\2\3:\13\3\2\2\2;<\7\r\2\2"+
		"<=\7\3\2\2=B\5\24\13\2>?\7\5\2\2?A\5\24\13\2@>\3\2\2\2AD\3\2\2\2B@\3\2"+
		"\2\2BC\3\2\2\2CE\3\2\2\2DB\3\2\2\2EF\7\4\2\2FG\7\2\2\3G\r\3\2\2\2HI\7"+
		"\16\2\2IJ\7\3\2\2JO\5\22\n\2KL\7\5\2\2LN\5\22\n\2MK\3\2\2\2NQ\3\2\2\2"+
		"OM\3\2\2\2OP\3\2\2\2PR\3\2\2\2QO\3\2\2\2RS\7\4\2\2ST\7\2\2\3T\17\3\2\2"+
		"\2U[\5\26\f\2VW\7\3\2\2WX\5\26\f\2XY\7\4\2\2Y[\3\2\2\2ZU\3\2\2\2ZV\3\2"+
		"\2\2[\21\3\2\2\2\\]\7\3\2\2]b\5\24\13\2^_\7\5\2\2_a\5\24\13\2`^\3\2\2"+
		"\2ad\3\2\2\2b`\3\2\2\2bc\3\2\2\2ce\3\2\2\2db\3\2\2\2ef\7\4\2\2f\23\3\2"+
		"\2\2gh\7\3\2\2hm\5\26\f\2ij\7\5\2\2jl\5\26\f\2ki\3\2\2\2lo\3\2\2\2mk\3"+
		"\2\2\2mn\3\2\2\2np\3\2\2\2om\3\2\2\2pq\7\4\2\2q\25\3\2\2\2rs\7\6\2\2s"+
		"t\7\6\2\2t\27\3\2\2\2\t\36\65BOZbm";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}