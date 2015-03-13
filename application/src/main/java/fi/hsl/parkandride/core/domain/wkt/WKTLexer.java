// Generated from /Users/samppa/git/parkandrideAPI/application/src/main/antlr/WKT.g4 by ANTLR 4.5
package fi.hsl.parkandride.core.domain.wkt;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class WKTLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		Decimal=1, IntegerPart=2, DecimalPart=3, COMMA=4, LPAR=5, RPAR=6, POINT=7, 
		LINESTRING=8, POLYGON=9, MULTIPOINT=10, MULTILINESTRING=11, MULTIPOLYGON=12, 
		WS=13;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"Decimal", "IntegerPart", "DecimalPart", "Digit", "NonZeroDigit", "DOT", 
		"COMMA", "LPAR", "RPAR", "POINT", "LINESTRING", "POLYGON", "MULTIPOINT", 
		"MULTILINESTRING", "MULTIPOLYGON", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, null, null, "','", "'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "Decimal", "IntegerPart", "DecimalPart", "COMMA", "LPAR", "RPAR", 
		"POINT", "LINESTRING", "POLYGON", "MULTIPOINT", "MULTILINESTRING", "MULTIPOLYGON", 
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


	public WKTLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "WKT.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\17\u010a\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\3\2\5"+
		"\2%\n\2\3\2\3\2\3\2\3\2\5\2+\n\2\3\3\3\3\3\3\7\3\60\n\3\f\3\16\3\63\13"+
		"\3\5\3\65\n\3\3\4\6\48\n\4\r\4\16\49\3\5\3\5\5\5>\n\5\3\6\3\6\3\7\3\7"+
		"\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\5\13Y\n\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3"+
		"\f\3\f\3\f\3\f\3\f\3\f\5\fy\n\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\5\r\u0090\n\r\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\5\16\u00b0\n\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\3\17\3\17\3\17\3\17\3\17\5\17\u00df\n\17\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\5\20\u0105\n\20\3\21\3\21\3\21\3\21\2\2\22\3\3\5\4\7\5"+
		"\t\2\13\2\r\2\17\6\21\7\23\b\25\t\27\n\31\13\33\f\35\r\37\16!\17\3\2\4"+
		"\3\2\63;\5\2\13\f\17\17\"\"\u0118\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2"+
		"\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31"+
		"\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\3$\3\2\2\2"+
		"\5\64\3\2\2\2\7\67\3\2\2\2\t=\3\2\2\2\13?\3\2\2\2\rA\3\2\2\2\17C\3\2\2"+
		"\2\21E\3\2\2\2\23G\3\2\2\2\25X\3\2\2\2\27x\3\2\2\2\31\u008f\3\2\2\2\33"+
		"\u00af\3\2\2\2\35\u00de\3\2\2\2\37\u0104\3\2\2\2!\u0106\3\2\2\2#%\7/\2"+
		"\2$#\3\2\2\2$%\3\2\2\2%&\3\2\2\2&*\5\5\3\2\'(\5\r\7\2()\5\7\4\2)+\3\2"+
		"\2\2*\'\3\2\2\2*+\3\2\2\2+\4\3\2\2\2,\65\7\62\2\2-\61\5\13\6\2.\60\5\t"+
		"\5\2/.\3\2\2\2\60\63\3\2\2\2\61/\3\2\2\2\61\62\3\2\2\2\62\65\3\2\2\2\63"+
		"\61\3\2\2\2\64,\3\2\2\2\64-\3\2\2\2\65\6\3\2\2\2\668\5\t\5\2\67\66\3\2"+
		"\2\289\3\2\2\29\67\3\2\2\29:\3\2\2\2:\b\3\2\2\2;>\7\62\2\2<>\5\13\6\2"+
		"=;\3\2\2\2=<\3\2\2\2>\n\3\2\2\2?@\t\2\2\2@\f\3\2\2\2AB\7\60\2\2B\16\3"+
		"\2\2\2CD\7.\2\2D\20\3\2\2\2EF\7*\2\2F\22\3\2\2\2GH\7+\2\2H\24\3\2\2\2"+
		"IJ\7R\2\2JK\7Q\2\2KL\7K\2\2LM\7P\2\2MY\7V\2\2NO\7r\2\2OP\7q\2\2PQ\7k\2"+
		"\2QR\7p\2\2RY\7v\2\2ST\7R\2\2TU\7q\2\2UV\7k\2\2VW\7p\2\2WY\7v\2\2XI\3"+
		"\2\2\2XN\3\2\2\2XS\3\2\2\2Y\26\3\2\2\2Z[\7N\2\2[\\\7K\2\2\\]\7P\2\2]^"+
		"\7G\2\2^_\7U\2\2_`\7V\2\2`a\7T\2\2ab\7K\2\2bc\7P\2\2cy\7I\2\2de\7n\2\2"+
		"ef\7k\2\2fg\7p\2\2gh\7g\2\2hi\7u\2\2ij\7v\2\2jk\7t\2\2kl\7k\2\2lm\7p\2"+
		"\2my\7i\2\2no\7N\2\2op\7k\2\2pq\7p\2\2qr\7g\2\2rs\7U\2\2st\7v\2\2tu\7"+
		"t\2\2uv\7k\2\2vw\7p\2\2wy\7i\2\2xZ\3\2\2\2xd\3\2\2\2xn\3\2\2\2y\30\3\2"+
		"\2\2z{\7R\2\2{|\7Q\2\2|}\7N\2\2}~\7[\2\2~\177\7I\2\2\177\u0080\7Q\2\2"+
		"\u0080\u0090\7P\2\2\u0081\u0082\7r\2\2\u0082\u0083\7q\2\2\u0083\u0084"+
		"\7n\2\2\u0084\u0085\7{\2\2\u0085\u0086\7i\2\2\u0086\u0087\7q\2\2\u0087"+
		"\u0090\7p\2\2\u0088\u0089\7R\2\2\u0089\u008a\7q\2\2\u008a\u008b\7n\2\2"+
		"\u008b\u008c\7{\2\2\u008c\u008d\7i\2\2\u008d\u008e\7q\2\2\u008e\u0090"+
		"\7p\2\2\u008fz\3\2\2\2\u008f\u0081\3\2\2\2\u008f\u0088\3\2\2\2\u0090\32"+
		"\3\2\2\2\u0091\u0092\7O\2\2\u0092\u0093\7W\2\2\u0093\u0094\7N\2\2\u0094"+
		"\u0095\7V\2\2\u0095\u0096\7K\2\2\u0096\u0097\7R\2\2\u0097\u0098\7Q\2\2"+
		"\u0098\u0099\7K\2\2\u0099\u009a\7P\2\2\u009a\u00b0\7V\2\2\u009b\u009c"+
		"\7o\2\2\u009c\u009d\7w\2\2\u009d\u009e\7n\2\2\u009e\u009f\7v\2\2\u009f"+
		"\u00a0\7k\2\2\u00a0\u00a1\7r\2\2\u00a1\u00a2\7q\2\2\u00a2\u00a3\7k\2\2"+
		"\u00a3\u00a4\7p\2\2\u00a4\u00b0\7v\2\2\u00a5\u00a6\7O\2\2\u00a6\u00a7"+
		"\7w\2\2\u00a7\u00a8\7n\2\2\u00a8\u00a9\7v\2\2\u00a9\u00aa\7k\2\2\u00aa"+
		"\u00ab\7R\2\2\u00ab\u00ac\7q\2\2\u00ac\u00ad\7k\2\2\u00ad\u00ae\7p\2\2"+
		"\u00ae\u00b0\7v\2\2\u00af\u0091\3\2\2\2\u00af\u009b\3\2\2\2\u00af\u00a5"+
		"\3\2\2\2\u00b0\34\3\2\2\2\u00b1\u00b2\7O\2\2\u00b2\u00b3\7W\2\2\u00b3"+
		"\u00b4\7N\2\2\u00b4\u00b5\7V\2\2\u00b5\u00b6\7K\2\2\u00b6\u00b7\7N\2\2"+
		"\u00b7\u00b8\7K\2\2\u00b8\u00b9\7P\2\2\u00b9\u00ba\7G\2\2\u00ba\u00bb"+
		"\7U\2\2\u00bb\u00bc\7V\2\2\u00bc\u00bd\7T\2\2\u00bd\u00be\7K\2\2\u00be"+
		"\u00bf\7P\2\2\u00bf\u00df\7I\2\2\u00c0\u00c1\7o\2\2\u00c1\u00c2\7w\2\2"+
		"\u00c2\u00c3\7n\2\2\u00c3\u00c4\7v\2\2\u00c4\u00c5\7k\2\2\u00c5\u00c6"+
		"\7n\2\2\u00c6\u00c7\7k\2\2\u00c7\u00c8\7p\2\2\u00c8\u00c9\7g\2\2\u00c9"+
		"\u00ca\7u\2\2\u00ca\u00cb\7v\2\2\u00cb\u00cc\7t\2\2\u00cc\u00cd\7k\2\2"+
		"\u00cd\u00ce\7p\2\2\u00ce\u00df\7i\2\2\u00cf\u00d0\7O\2\2\u00d0\u00d1"+
		"\7w\2\2\u00d1\u00d2\7n\2\2\u00d2\u00d3\7v\2\2\u00d3\u00d4\7k\2\2\u00d4"+
		"\u00d5\7N\2\2\u00d5\u00d6\7k\2\2\u00d6\u00d7\7p\2\2\u00d7\u00d8\7g\2\2"+
		"\u00d8\u00d9\7U\2\2\u00d9\u00da\7v\2\2\u00da\u00db\7t\2\2\u00db\u00dc"+
		"\7k\2\2\u00dc\u00dd\7p\2\2\u00dd\u00df\7i\2\2\u00de\u00b1\3\2\2\2\u00de"+
		"\u00c0\3\2\2\2\u00de\u00cf\3\2\2\2\u00df\36\3\2\2\2\u00e0\u00e1\7O\2\2"+
		"\u00e1\u00e2\7W\2\2\u00e2\u00e3\7N\2\2\u00e3\u00e4\7V\2\2\u00e4\u00e5"+
		"\7K\2\2\u00e5\u00e6\7R\2\2\u00e6\u00e7\7Q\2\2\u00e7\u00e8\7N\2\2\u00e8"+
		"\u00e9\7[\2\2\u00e9\u00ea\7I\2\2\u00ea\u00eb\7Q\2\2\u00eb\u0105\7P\2\2"+
		"\u00ec\u00ed\7o\2\2\u00ed\u00ee\7w\2\2\u00ee\u00ef\7n\2\2\u00ef\u00f0"+
		"\7v\2\2\u00f0\u00f1\7k\2\2\u00f1\u00f2\7r\2\2\u00f2\u00f3\7q\2\2\u00f3"+
		"\u00f4\7n\2\2\u00f4\u00f5\7{\2\2\u00f5\u00f6\7i\2\2\u00f6\u00f7\7q\2\2"+
		"\u00f7\u0105\7p\2\2\u00f8\u00f9\7O\2\2\u00f9\u00fa\7w\2\2\u00fa\u00fb"+
		"\7n\2\2\u00fb\u00fc\7v\2\2\u00fc\u00fd\7k\2\2\u00fd\u00fe\7R\2\2\u00fe"+
		"\u00ff\7q\2\2\u00ff\u0100\7n\2\2\u0100\u0101\7{\2\2\u0101\u0102\7i\2\2"+
		"\u0102\u0103\7q\2\2\u0103\u0105\7p\2\2\u0104\u00e0\3\2\2\2\u0104\u00ec"+
		"\3\2\2\2\u0104\u00f8\3\2\2\2\u0105 \3\2\2\2\u0106\u0107\t\3\2\2\u0107"+
		"\u0108\3\2\2\2\u0108\u0109\b\21\2\2\u0109\"\3\2\2\2\17\2$*\61\649=Xx\u008f"+
		"\u00af\u00de\u0104\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}