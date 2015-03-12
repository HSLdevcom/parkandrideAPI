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
		T__0=1, T__1=2, T__2=3, Decimal=4, IntegerPart=5, DecimalPart=6, POINT=7, 
		LINESTRING=8, POLYGON=9, MULTIPOINT=10, MULTILINESTRING=11, MULTIPOLYGON=12, 
		WS=13;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "Decimal", "IntegerPart", "DecimalPart", "Digit", 
		"NonZeroDigit", "DOT", "COMMA", "POINT", "LINESTRING", "POLYGON", "MULTIPOINT", 
		"MULTILINESTRING", "MULTIPOLYGON", "WS"
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\17\u010b\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\5\5\5\60\n\5\3\6\3\6\3\6\7"+
		"\6\65\n\6\f\6\16\68\13\6\5\6:\n\6\3\7\6\7=\n\7\r\7\16\7>\3\b\3\b\5\bC"+
		"\n\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\5\fZ\n\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r"+
		"\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\3\r\5\rz\n\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u0091\n\16"+
		"\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\3\17\5\17\u00b1\n\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\5\20\u00e0\n\20\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\5\21\u0106\n\21\3\22\3\22\3\22\3\22\2\2\23\3"+
		"\3\5\4\7\5\t\6\13\7\r\b\17\2\21\2\23\2\25\2\27\t\31\n\33\13\35\f\37\r"+
		"!\16#\17\3\2\4\3\2\63;\5\2\13\f\17\17\"\"\u0117\2\3\3\2\2\2\2\5\3\2\2"+
		"\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\27\3\2\2\2\2\31"+
		"\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2"+
		"\3%\3\2\2\2\5\'\3\2\2\2\7)\3\2\2\2\t+\3\2\2\2\139\3\2\2\2\r<\3\2\2\2\17"+
		"B\3\2\2\2\21D\3\2\2\2\23F\3\2\2\2\25H\3\2\2\2\27Y\3\2\2\2\31y\3\2\2\2"+
		"\33\u0090\3\2\2\2\35\u00b0\3\2\2\2\37\u00df\3\2\2\2!\u0105\3\2\2\2#\u0107"+
		"\3\2\2\2%&\7*\2\2&\4\3\2\2\2\'(\7+\2\2(\6\3\2\2\2)*\7.\2\2*\b\3\2\2\2"+
		"+/\5\13\6\2,-\5\23\n\2-.\5\r\7\2.\60\3\2\2\2/,\3\2\2\2/\60\3\2\2\2\60"+
		"\n\3\2\2\2\61:\7\62\2\2\62\66\5\21\t\2\63\65\5\17\b\2\64\63\3\2\2\2\65"+
		"8\3\2\2\2\66\64\3\2\2\2\66\67\3\2\2\2\67:\3\2\2\28\66\3\2\2\29\61\3\2"+
		"\2\29\62\3\2\2\2:\f\3\2\2\2;=\5\17\b\2<;\3\2\2\2=>\3\2\2\2><\3\2\2\2>"+
		"?\3\2\2\2?\16\3\2\2\2@C\7\62\2\2AC\5\21\t\2B@\3\2\2\2BA\3\2\2\2C\20\3"+
		"\2\2\2DE\t\2\2\2E\22\3\2\2\2FG\7\60\2\2G\24\3\2\2\2HI\7.\2\2I\26\3\2\2"+
		"\2JK\7R\2\2KL\7Q\2\2LM\7K\2\2MN\7P\2\2NZ\7V\2\2OP\7r\2\2PQ\7q\2\2QR\7"+
		"k\2\2RS\7p\2\2SZ\7v\2\2TU\7R\2\2UV\7q\2\2VW\7k\2\2WX\7p\2\2XZ\7v\2\2Y"+
		"J\3\2\2\2YO\3\2\2\2YT\3\2\2\2Z\30\3\2\2\2[\\\7N\2\2\\]\7K\2\2]^\7P\2\2"+
		"^_\7G\2\2_`\7U\2\2`a\7V\2\2ab\7T\2\2bc\7K\2\2cd\7P\2\2dz\7I\2\2ef\7n\2"+
		"\2fg\7k\2\2gh\7p\2\2hi\7g\2\2ij\7u\2\2jk\7v\2\2kl\7t\2\2lm\7k\2\2mn\7"+
		"p\2\2nz\7i\2\2op\7N\2\2pq\7k\2\2qr\7p\2\2rs\7g\2\2st\7U\2\2tu\7v\2\2u"+
		"v\7t\2\2vw\7k\2\2wx\7p\2\2xz\7i\2\2y[\3\2\2\2ye\3\2\2\2yo\3\2\2\2z\32"+
		"\3\2\2\2{|\7R\2\2|}\7Q\2\2}~\7N\2\2~\177\7[\2\2\177\u0080\7I\2\2\u0080"+
		"\u0081\7Q\2\2\u0081\u0091\7P\2\2\u0082\u0083\7r\2\2\u0083\u0084\7q\2\2"+
		"\u0084\u0085\7n\2\2\u0085\u0086\7{\2\2\u0086\u0087\7i\2\2\u0087\u0088"+
		"\7q\2\2\u0088\u0091\7p\2\2\u0089\u008a\7R\2\2\u008a\u008b\7q\2\2\u008b"+
		"\u008c\7n\2\2\u008c\u008d\7{\2\2\u008d\u008e\7i\2\2\u008e\u008f\7q\2\2"+
		"\u008f\u0091\7p\2\2\u0090{\3\2\2\2\u0090\u0082\3\2\2\2\u0090\u0089\3\2"+
		"\2\2\u0091\34\3\2\2\2\u0092\u0093\7O\2\2\u0093\u0094\7W\2\2\u0094\u0095"+
		"\7N\2\2\u0095\u0096\7V\2\2\u0096\u0097\7K\2\2\u0097\u0098\7R\2\2\u0098"+
		"\u0099\7Q\2\2\u0099\u009a\7K\2\2\u009a\u009b\7P\2\2\u009b\u00b1\7V\2\2"+
		"\u009c\u009d\7o\2\2\u009d\u009e\7w\2\2\u009e\u009f\7n\2\2\u009f\u00a0"+
		"\7v\2\2\u00a0\u00a1\7k\2\2\u00a1\u00a2\7r\2\2\u00a2\u00a3\7q\2\2\u00a3"+
		"\u00a4\7k\2\2\u00a4\u00a5\7p\2\2\u00a5\u00b1\7v\2\2\u00a6\u00a7\7O\2\2"+
		"\u00a7\u00a8\7w\2\2\u00a8\u00a9\7n\2\2\u00a9\u00aa\7v\2\2\u00aa\u00ab"+
		"\7k\2\2\u00ab\u00ac\7R\2\2\u00ac\u00ad\7q\2\2\u00ad\u00ae\7k\2\2\u00ae"+
		"\u00af\7p\2\2\u00af\u00b1\7v\2\2\u00b0\u0092\3\2\2\2\u00b0\u009c\3\2\2"+
		"\2\u00b0\u00a6\3\2\2\2\u00b1\36\3\2\2\2\u00b2\u00b3\7O\2\2\u00b3\u00b4"+
		"\7W\2\2\u00b4\u00b5\7N\2\2\u00b5\u00b6\7V\2\2\u00b6\u00b7\7K\2\2\u00b7"+
		"\u00b8\7N\2\2\u00b8\u00b9\7K\2\2\u00b9\u00ba\7P\2\2\u00ba\u00bb\7G\2\2"+
		"\u00bb\u00bc\7U\2\2\u00bc\u00bd\7V\2\2\u00bd\u00be\7T\2\2\u00be\u00bf"+
		"\7K\2\2\u00bf\u00c0\7P\2\2\u00c0\u00e0\7I\2\2\u00c1\u00c2\7o\2\2\u00c2"+
		"\u00c3\7w\2\2\u00c3\u00c4\7n\2\2\u00c4\u00c5\7v\2\2\u00c5\u00c6\7k\2\2"+
		"\u00c6\u00c7\7n\2\2\u00c7\u00c8\7k\2\2\u00c8\u00c9\7p\2\2\u00c9\u00ca"+
		"\7g\2\2\u00ca\u00cb\7u\2\2\u00cb\u00cc\7v\2\2\u00cc\u00cd\7t\2\2\u00cd"+
		"\u00ce\7k\2\2\u00ce\u00cf\7p\2\2\u00cf\u00e0\7i\2\2\u00d0\u00d1\7O\2\2"+
		"\u00d1\u00d2\7w\2\2\u00d2\u00d3\7n\2\2\u00d3\u00d4\7v\2\2\u00d4\u00d5"+
		"\7k\2\2\u00d5\u00d6\7N\2\2\u00d6\u00d7\7k\2\2\u00d7\u00d8\7p\2\2\u00d8"+
		"\u00d9\7g\2\2\u00d9\u00da\7U\2\2\u00da\u00db\7v\2\2\u00db\u00dc\7t\2\2"+
		"\u00dc\u00dd\7k\2\2\u00dd\u00de\7p\2\2\u00de\u00e0\7i\2\2\u00df\u00b2"+
		"\3\2\2\2\u00df\u00c1\3\2\2\2\u00df\u00d0\3\2\2\2\u00e0 \3\2\2\2\u00e1"+
		"\u00e2\7O\2\2\u00e2\u00e3\7W\2\2\u00e3\u00e4\7N\2\2\u00e4\u00e5\7V\2\2"+
		"\u00e5\u00e6\7K\2\2\u00e6\u00e7\7R\2\2\u00e7\u00e8\7Q\2\2\u00e8\u00e9"+
		"\7N\2\2\u00e9\u00ea\7[\2\2\u00ea\u00eb\7I\2\2\u00eb\u00ec\7Q\2\2\u00ec"+
		"\u0106\7P\2\2\u00ed\u00ee\7o\2\2\u00ee\u00ef\7w\2\2\u00ef\u00f0\7n\2\2"+
		"\u00f0\u00f1\7v\2\2\u00f1\u00f2\7k\2\2\u00f2\u00f3\7r\2\2\u00f3\u00f4"+
		"\7q\2\2\u00f4\u00f5\7n\2\2\u00f5\u00f6\7{\2\2\u00f6\u00f7\7i\2\2\u00f7"+
		"\u00f8\7q\2\2\u00f8\u0106\7p\2\2\u00f9\u00fa\7O\2\2\u00fa\u00fb\7w\2\2"+
		"\u00fb\u00fc\7n\2\2\u00fc\u00fd\7v\2\2\u00fd\u00fe\7k\2\2\u00fe\u00ff"+
		"\7R\2\2\u00ff\u0100\7q\2\2\u0100\u0101\7n\2\2\u0101\u0102\7{\2\2\u0102"+
		"\u0103\7i\2\2\u0103\u0104\7q\2\2\u0104\u0106\7p\2\2\u0105\u00e1\3\2\2"+
		"\2\u0105\u00ed\3\2\2\2\u0105\u00f9\3\2\2\2\u0106\"\3\2\2\2\u0107\u0108"+
		"\t\3\2\2\u0108\u0109\3\2\2\2\u0109\u010a\b\22\2\2\u010a$\3\2\2\2\16\2"+
		"/\669>BYy\u0090\u00b0\u00df\u0105\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}