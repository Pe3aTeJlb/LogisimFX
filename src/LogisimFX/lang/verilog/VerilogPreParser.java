/*
 * This file is part of LogisimFX. Copyright (c) 2023, Pplos Studio
 * License information is located in the Launch file
 */

// Generated from java-escape by ANTLR 4.11.1

package LogisimFX.lang.verilog;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class VerilogPreParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.11.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		ALWAYS=1, AM=2, AMAM=3, AMAMAM=4, AND=5, AS=6, ASAS=7, ASGT=8, ASSIGN=9, 
		AT=10, AUTOMATIC=11, BEGIN=12, BUF=13, BUFIFONE=14, BUFIFZERO=15, CA=16, 
		CASE=17, CASEX=18, CASEZ=19, CATI=20, CELL=21, CL=22, CMOS=23, CO=24, 
		CONFIG=25, DEASSIGN=26, DEFAULT=27, DEFPARAM=28, DESIGN=29, DISABLE=30, 
		DL=31, DLFULLSKEW=32, DLHOLD=33, DLNOCHANGE=34, DLPERIOD=35, DLRECOVERY=36, 
		DLRECREM=37, DLREMOVAL=38, DLSETUP=39, DLSETUPHOLD=40, DLSKEW=41, DLTIMESKEW=42, 
		DLWIDTH=43, DQ=44, DT=45, EDGE=46, ELSE=47, EM=48, EMEQ=49, EMEQEQ=50, 
		END=51, ENDCASE=52, ENDCONFIG=53, ENDFUNCTION=54, ENDGENERATE=55, ENDMODULE=56, 
		ENDPRIMITIVE=57, ENDSPECIFY=58, ENDTABLE=59, ENDTASK=60, EQ=61, EQEQ=62, 
		EQEQEQ=63, EQGT=64, EVENT=65, FOR=66, FORCE=67, FOREVER=68, FORK=69, FUNCTION=70, 
		GA=71, GENERATE=72, GENVAR=73, GT=74, GTEQ=75, GTGT=76, GTGTGT=77, HA=78, 
		HIGHZONE=79, HIGHZZERO=80, IF=81, IFNONE=82, INCLUDE=83, INITIAL=84, INOUT=85, 
		INPUT=86, INSTANCE=87, INTEGER=88, JOIN=89, LARGE=90, LB=91, LC=92, LIBLIST=93, 
		LIBRARY=94, LOCALPARAM=95, LP=96, LT=97, LTEQ=98, LTLT=99, LTLTLT=100, 
		MACROMODULE=101, MEDIUM=102, MI=103, MICL=104, MIGT=105, MIINCDIR=106, 
		MO=107, MODULE=108, NAND=109, NEGEDGE=110, NMOS=111, NOR=112, NOSHOWCANCELLED=113, 
		NOT=114, NOTIFONE=115, NOTIFZERO=116, OR=117, OUTPUT=118, PARAMETER=119, 
		PATHPULSEDL=120, PL=121, PLCL=122, PMOS=123, POSEDGE=124, PRIMITIVE=125, 
		PULLDOWN=126, PULLONE=127, PULLUP=128, PULLZERO=129, PULSESTYLE_ONDETECT=130, 
		PULSESTYLE_ONEVENT=131, QM=132, RB=133, RC=134, RCMOS=135, REAL=136, REALTIME=137, 
		REG=138, RELEASE=139, REPEAT=140, RNMOS=141, RP=142, RPMOS=143, RTRAN=144, 
		RTRANIFONE=145, RTRANIFZERO=146, SC=147, SCALARED=148, SHOWCANCELLED=149, 
		SIGNED=150, SL=151, SMALL=152, SPECIFY=153, SPECPARAM=154, STRONGONE=155, 
		STRONGZERO=156, SUPPLYONE=157, SUPPLYZERO=158, TABLE=159, TASK=160, TI=161, 
		TIAM=162, TICA=163, TIME=164, TIVL=165, TRAN=166, TRANIFONE=167, TRANIFZERO=168, 
		TRI=169, TRIAND=170, TRIONE=171, TRIOR=172, TRIREG=173, TRIZERO=174, USE=175, 
		UWIRE=176, VECTORED=177, VL=178, VLVL=179, WAIT=180, WAND=181, WEAKONE=182, 
		WEAKZERO=183, WHILE=184, WIRE=185, WOR=186, XNOR=187, XOR=188, BINARY_BASE=189, 
		COMMENT=190, DECIMAL_BASE=191, ESCAPED_IDENTIFIER=192, EXPONENTIAL_NUMBER=193, 
		FIXED_POINT_NUMBER=194, HEX_BASE=195, OCTAL_BASE=196, SIMPLE_IDENTIFIER=197, 
		STRING=198, SYSTEM_TF_IDENTIFIER=199, UNSIGNED_NUMBER=200, WHITE_SPACE=201, 
		BINARY_VALUE=202, X_OR_Z_UNDERSCORE=203, EDGE_DESCRIPTOR=204, HEX_VALUE=205, 
		FILE_PATH_SPEC=206, OCTAL_VALUE=207, EDGE_SYMBOL=208, LEVEL_ONLY_SYMBOL=209, 
		OUTPUT_OR_LEVEL_SYMBOL=210, BEGIN_KEYWORDS_DIRECTIVE=211, CELLDEFINE_DIRECTIVE=212, 
		DEFAULT_NETTYPE_DIRECTIVE=213, DEFINE_DIRECTIVE=214, ELSE_DIRECTIVE=215, 
		ELSIF_DIRECTIVE=216, END_KEYWORDS_DIRECTIVE=217, ENDCELLDEFINE_DIRECTIVE=218, 
		ENDIF_DIRECTIVE=219, IFDEF_DIRECTIVE=220, IFNDEF_DIRECTIVE=221, INCLUDE_DIRECTIVE=222, 
		LINE_DIRECTIVE=223, NOUNCONNECTED_DRIVE_DIRECTIVE=224, PRAGMA_DIRECTIVE=225, 
		RESETALL_DIRECTIVE=226, TIMESCALE_DIRECTIVE=227, UNCONNECTED_DRIVE_DIRECTIVE=228, 
		UNDEF_DIRECTIVE=229, MACRO_USAGE=230, VERSION_SPECIFIER=231, DEFAULT_NETTYPE_VALUE=232, 
		MACRO_NAME=233, FILENAME=234, MACRO_DELIMITER=235, MACRO_ESC_NEWLINE=236, 
		MACRO_ESC_QUOTE=237, MACRO_QUOTE=238, MACRO_TEXT=239, SOURCE_TEXT=240, 
		TIME_UNIT=241, TIME_VALUE=242, UNCONNECTED_DRIVE_VALUE=243, MACRO_IDENTIFIER=244;
	public static final int
		RULE_source_text = 0, RULE_compiler_directive = 1, RULE_begin_keywords_directive = 2, 
		RULE_celldefine_directive = 3, RULE_default_nettype_directive = 4, RULE_default_nettype_value = 5, 
		RULE_else_directive = 6, RULE_elsif_directive = 7, RULE_end_keywords_directive = 8, 
		RULE_endcelldefine_directive = 9, RULE_endif_directive = 10, RULE_filename = 11, 
		RULE_group_of_lines = 12, RULE_identifier = 13, RULE_ifdef_directive = 14, 
		RULE_ifndef_directive = 15, RULE_include_directive = 16, RULE_level = 17, 
		RULE_line_directive = 18, RULE_macro_delimiter = 19, RULE_macro_esc_newline = 20, 
		RULE_macro_esc_quote = 21, RULE_macro_identifier = 22, RULE_macro_name = 23, 
		RULE_macro_quote = 24, RULE_macro_text = 25, RULE_macro_usage = 26, RULE_nounconnected_drive_directive = 27, 
		RULE_number = 28, RULE_pragma_directive = 29, RULE_pragma_expression = 30, 
		RULE_pragma_keyword = 31, RULE_pragma_name = 32, RULE_pragma_value = 33, 
		RULE_resetall_directive = 34, RULE_source_text_ = 35, RULE_string_ = 36, 
		RULE_text_macro_definition = 37, RULE_text_macro_usage = 38, RULE_time_precision = 39, 
		RULE_time_unit = 40, RULE_timescale_directive = 41, RULE_unconnected_drive_directive = 42, 
		RULE_unconnected_drive_value = 43, RULE_undef_directive = 44, RULE_version_specifier = 45;
	private static String[] makeRuleNames() {
		return new String[] {
			"source_text", "compiler_directive", "begin_keywords_directive", "celldefine_directive", 
			"default_nettype_directive", "default_nettype_value", "else_directive", 
			"elsif_directive", "end_keywords_directive", "endcelldefine_directive", 
			"endif_directive", "filename", "group_of_lines", "identifier", "ifdef_directive", 
			"ifndef_directive", "include_directive", "level", "line_directive", "macro_delimiter", 
			"macro_esc_newline", "macro_esc_quote", "macro_identifier", "macro_name", 
			"macro_quote", "macro_text", "macro_usage", "nounconnected_drive_directive", 
			"number", "pragma_directive", "pragma_expression", "pragma_keyword", 
			"pragma_name", "pragma_value", "resetall_directive", "source_text_", 
			"string_", "text_macro_definition", "text_macro_usage", "time_precision", 
			"time_unit", "timescale_directive", "unconnected_drive_directive", "unconnected_drive_value", 
			"undef_directive", "version_specifier"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'always'", "'&'", "'&&'", "'&&&'", "'and'", "'*'", "'**'", "'*>'", 
			"'assign'", "'@'", "'automatic'", "'begin'", "'buf'", "'bufif1'", "'bufif0'", 
			"'^'", "'case'", "'casex'", "'casez'", "'^~'", "'cell'", "':'", "'cmos'", 
			"','", "'config'", "'deassign'", "'default'", "'defparam'", "'design'", 
			"'disable'", "'$'", "'$fullskew'", "'$hold'", "'$nochange'", "'$period'", 
			"'$recovery'", "'$recrem'", "'$removal'", "'$setup'", "'$setuphold'", 
			"'$skew'", "'$timeskew'", "'$width'", "'\"'", "'.'", "'edge'", "'else'", 
			"'!'", "'!='", "'!=='", "'end'", "'endcase'", "'endconfig'", "'endfunction'", 
			"'endgenerate'", "'endmodule'", "'endprimitive'", "'endspecify'", "'endtable'", 
			"'endtask'", "'='", "'=='", "'==='", "'=>'", "'event'", "'for'", "'force'", 
			"'forever'", "'fork'", "'function'", null, "'generate'", "'genvar'", 
			"'>'", "'>='", "'>>'", "'>>>'", "'#'", "'highz1'", "'highz0'", "'if'", 
			"'ifnone'", "'include'", "'initial'", "'inout'", "'input'", "'instance'", 
			"'integer'", "'join'", "'large'", "'['", "'{'", "'liblist'", "'library'", 
			"'localparam'", "'('", "'<'", "'<='", "'<<'", "'<<<'", "'macromodule'", 
			"'medium'", "'-'", "'-:'", "'->'", "'-incdir'", "'%'", "'module'", "'nand'", 
			"'negedge'", "'nmos'", "'nor'", "'noshowcancelled'", "'not'", "'notif1'", 
			"'notif0'", "'or'", "'output'", "'parameter'", "'PATHPULSE$'", "'+'", 
			"'+:'", "'pmos'", "'posedge'", "'primitive'", "'pulldown'", "'pull1'", 
			"'pullup'", "'pull0'", "'pulsestyle_ondetect'", "'pulsestyle_onevent'", 
			"'?'", "']'", "'}'", "'rcmos'", "'real'", "'realtime'", "'reg'", "'release'", 
			"'repeat'", "'rnmos'", "')'", "'rpmos'", "'rtran'", "'rtranif1'", "'rtranif0'", 
			"';'", "'scalared'", "'showcancelled'", "'signed'", "'/'", "'small'", 
			"'specify'", "'specparam'", "'strong1'", "'strong0'", "'supply1'", "'supply0'", 
			"'table'", "'task'", "'~'", "'~&'", "'~^'", "'time'", "'~|'", "'tran'", 
			"'tranif1'", "'tranif0'", "'tri'", "'triand'", "'tri1'", "'trior'", "'trireg'", 
			"'tri0'", "'use'", "'uwire'", "'vectored'", "'|'", "'||'", "'wait'", 
			"'wand'", "'weak1'", "'weak0'", "'while'", "'wire'", "'wor'", "'xnor'", 
			"'xor'", null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, "'celldefine'", null, null, null, null, "'end_keywords'", "'endcelldefine'", 
			null, null, null, null, null, "'nounconnected_drive'", null, "'resetall'", 
			null, null, null, null, null, null, null, null, "'``'", null, "'`\\`\"'", 
			"'`\"'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ALWAYS", "AM", "AMAM", "AMAMAM", "AND", "AS", "ASAS", "ASGT", 
			"ASSIGN", "AT", "AUTOMATIC", "BEGIN", "BUF", "BUFIFONE", "BUFIFZERO", 
			"CA", "CASE", "CASEX", "CASEZ", "CATI", "CELL", "CL", "CMOS", "CO", "CONFIG", 
			"DEASSIGN", "DEFAULT", "DEFPARAM", "DESIGN", "DISABLE", "DL", "DLFULLSKEW", 
			"DLHOLD", "DLNOCHANGE", "DLPERIOD", "DLRECOVERY", "DLRECREM", "DLREMOVAL", 
			"DLSETUP", "DLSETUPHOLD", "DLSKEW", "DLTIMESKEW", "DLWIDTH", "DQ", "DT", 
			"EDGE", "ELSE", "EM", "EMEQ", "EMEQEQ", "END", "ENDCASE", "ENDCONFIG", 
			"ENDFUNCTION", "ENDGENERATE", "ENDMODULE", "ENDPRIMITIVE", "ENDSPECIFY", 
			"ENDTABLE", "ENDTASK", "EQ", "EQEQ", "EQEQEQ", "EQGT", "EVENT", "FOR", 
			"FORCE", "FOREVER", "FORK", "FUNCTION", "GA", "GENERATE", "GENVAR", "GT", 
			"GTEQ", "GTGT", "GTGTGT", "HA", "HIGHZONE", "HIGHZZERO", "IF", "IFNONE", 
			"INCLUDE", "INITIAL", "INOUT", "INPUT", "INSTANCE", "INTEGER", "JOIN", 
			"LARGE", "LB", "LC", "LIBLIST", "LIBRARY", "LOCALPARAM", "LP", "LT", 
			"LTEQ", "LTLT", "LTLTLT", "MACROMODULE", "MEDIUM", "MI", "MICL", "MIGT", 
			"MIINCDIR", "MO", "MODULE", "NAND", "NEGEDGE", "NMOS", "NOR", "NOSHOWCANCELLED", 
			"NOT", "NOTIFONE", "NOTIFZERO", "OR", "OUTPUT", "PARAMETER", "PATHPULSEDL", 
			"PL", "PLCL", "PMOS", "POSEDGE", "PRIMITIVE", "PULLDOWN", "PULLONE", 
			"PULLUP", "PULLZERO", "PULSESTYLE_ONDETECT", "PULSESTYLE_ONEVENT", "QM", 
			"RB", "RC", "RCMOS", "REAL", "REALTIME", "REG", "RELEASE", "REPEAT", 
			"RNMOS", "RP", "RPMOS", "RTRAN", "RTRANIFONE", "RTRANIFZERO", "SC", "SCALARED", 
			"SHOWCANCELLED", "SIGNED", "SL", "SMALL", "SPECIFY", "SPECPARAM", "STRONGONE", 
			"STRONGZERO", "SUPPLYONE", "SUPPLYZERO", "TABLE", "TASK", "TI", "TIAM", 
			"TICA", "TIME", "TIVL", "TRAN", "TRANIFONE", "TRANIFZERO", "TRI", "TRIAND", 
			"TRIONE", "TRIOR", "TRIREG", "TRIZERO", "USE", "UWIRE", "VECTORED", "VL", 
			"VLVL", "WAIT", "WAND", "WEAKONE", "WEAKZERO", "WHILE", "WIRE", "WOR", 
			"XNOR", "XOR", "BINARY_BASE", "COMMENT", "DECIMAL_BASE", "ESCAPED_IDENTIFIER", 
			"EXPONENTIAL_NUMBER", "FIXED_POINT_NUMBER", "HEX_BASE", "OCTAL_BASE", 
			"SIMPLE_IDENTIFIER", "STRING", "SYSTEM_TF_IDENTIFIER", "UNSIGNED_NUMBER", 
			"WHITE_SPACE", "BINARY_VALUE", "X_OR_Z_UNDERSCORE", "EDGE_DESCRIPTOR", 
			"HEX_VALUE", "FILE_PATH_SPEC", "OCTAL_VALUE", "EDGE_SYMBOL", "LEVEL_ONLY_SYMBOL", 
			"OUTPUT_OR_LEVEL_SYMBOL", "BEGIN_KEYWORDS_DIRECTIVE", "CELLDEFINE_DIRECTIVE", 
			"DEFAULT_NETTYPE_DIRECTIVE", "DEFINE_DIRECTIVE", "ELSE_DIRECTIVE", "ELSIF_DIRECTIVE", 
			"END_KEYWORDS_DIRECTIVE", "ENDCELLDEFINE_DIRECTIVE", "ENDIF_DIRECTIVE", 
			"IFDEF_DIRECTIVE", "IFNDEF_DIRECTIVE", "INCLUDE_DIRECTIVE", "LINE_DIRECTIVE", 
			"NOUNCONNECTED_DRIVE_DIRECTIVE", "PRAGMA_DIRECTIVE", "RESETALL_DIRECTIVE", 
			"TIMESCALE_DIRECTIVE", "UNCONNECTED_DRIVE_DIRECTIVE", "UNDEF_DIRECTIVE", 
			"MACRO_USAGE", "VERSION_SPECIFIER", "DEFAULT_NETTYPE_VALUE", "MACRO_NAME", 
			"FILENAME", "MACRO_DELIMITER", "MACRO_ESC_NEWLINE", "MACRO_ESC_QUOTE", 
			"MACRO_QUOTE", "MACRO_TEXT", "SOURCE_TEXT", "TIME_UNIT", "TIME_VALUE", 
			"UNCONNECTED_DRIVE_VALUE", "MACRO_IDENTIFIER"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
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

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "java-escape"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public VerilogPreParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Source_textContext extends ParserRuleContext {
		public List<Compiler_directiveContext> compiler_directive() {
			return getRuleContexts(Compiler_directiveContext.class);
		}
		public Compiler_directiveContext compiler_directive(int i) {
			return getRuleContext(Compiler_directiveContext.class,i);
		}
		public Source_textContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_source_text; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterSource_text(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitSource_text(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitSource_text(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Source_textContext source_text() throws RecognitionException {
		Source_textContext _localctx = new Source_textContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_source_text);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==GA) {
				{
				{
				setState(92);
				compiler_directive();
				}
				}
				setState(97);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Compiler_directiveContext extends ParserRuleContext {
		public Begin_keywords_directiveContext begin_keywords_directive() {
			return getRuleContext(Begin_keywords_directiveContext.class,0);
		}
		public Celldefine_directiveContext celldefine_directive() {
			return getRuleContext(Celldefine_directiveContext.class,0);
		}
		public Default_nettype_directiveContext default_nettype_directive() {
			return getRuleContext(Default_nettype_directiveContext.class,0);
		}
		public End_keywords_directiveContext end_keywords_directive() {
			return getRuleContext(End_keywords_directiveContext.class,0);
		}
		public Endcelldefine_directiveContext endcelldefine_directive() {
			return getRuleContext(Endcelldefine_directiveContext.class,0);
		}
		public Ifdef_directiveContext ifdef_directive() {
			return getRuleContext(Ifdef_directiveContext.class,0);
		}
		public Ifndef_directiveContext ifndef_directive() {
			return getRuleContext(Ifndef_directiveContext.class,0);
		}
		public Include_directiveContext include_directive() {
			return getRuleContext(Include_directiveContext.class,0);
		}
		public Line_directiveContext line_directive() {
			return getRuleContext(Line_directiveContext.class,0);
		}
		public Nounconnected_drive_directiveContext nounconnected_drive_directive() {
			return getRuleContext(Nounconnected_drive_directiveContext.class,0);
		}
		public Pragma_directiveContext pragma_directive() {
			return getRuleContext(Pragma_directiveContext.class,0);
		}
		public Resetall_directiveContext resetall_directive() {
			return getRuleContext(Resetall_directiveContext.class,0);
		}
		public Text_macro_definitionContext text_macro_definition() {
			return getRuleContext(Text_macro_definitionContext.class,0);
		}
		public Text_macro_usageContext text_macro_usage() {
			return getRuleContext(Text_macro_usageContext.class,0);
		}
		public Timescale_directiveContext timescale_directive() {
			return getRuleContext(Timescale_directiveContext.class,0);
		}
		public Unconnected_drive_directiveContext unconnected_drive_directive() {
			return getRuleContext(Unconnected_drive_directiveContext.class,0);
		}
		public Undef_directiveContext undef_directive() {
			return getRuleContext(Undef_directiveContext.class,0);
		}
		public Compiler_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compiler_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterCompiler_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitCompiler_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitCompiler_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Compiler_directiveContext compiler_directive() throws RecognitionException {
		Compiler_directiveContext _localctx = new Compiler_directiveContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_compiler_directive);
		try {
			setState(115);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(98);
				begin_keywords_directive();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(99);
				celldefine_directive();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(100);
				default_nettype_directive();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(101);
				end_keywords_directive();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(102);
				endcelldefine_directive();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(103);
				ifdef_directive();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(104);
				ifndef_directive();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(105);
				include_directive();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(106);
				line_directive();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(107);
				nounconnected_drive_directive();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(108);
				pragma_directive();
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(109);
				resetall_directive();
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(110);
				text_macro_definition();
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(111);
				text_macro_usage();
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(112);
				timescale_directive();
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(113);
				unconnected_drive_directive();
				}
				break;
			case 17:
				enterOuterAlt(_localctx, 17);
				{
				setState(114);
				undef_directive();
				}
				break;
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

	@SuppressWarnings("CheckReturnValue")
	public static class Begin_keywords_directiveContext extends ParserRuleContext {
		public TerminalNode GA() { return getToken(VerilogPreParser.GA, 0); }
		public TerminalNode BEGIN_KEYWORDS_DIRECTIVE() { return getToken(VerilogPreParser.BEGIN_KEYWORDS_DIRECTIVE, 0); }
		public List<TerminalNode> DQ() { return getTokens(VerilogPreParser.DQ); }
		public TerminalNode DQ(int i) {
			return getToken(VerilogPreParser.DQ, i);
		}
		public Version_specifierContext version_specifier() {
			return getRuleContext(Version_specifierContext.class,0);
		}
		public Begin_keywords_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_begin_keywords_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterBegin_keywords_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitBegin_keywords_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitBegin_keywords_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Begin_keywords_directiveContext begin_keywords_directive() throws RecognitionException {
		Begin_keywords_directiveContext _localctx = new Begin_keywords_directiveContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_begin_keywords_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(117);
			match(GA);
			setState(118);
			match(BEGIN_KEYWORDS_DIRECTIVE);
			setState(119);
			match(DQ);
			setState(120);
			version_specifier();
			setState(121);
			match(DQ);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Celldefine_directiveContext extends ParserRuleContext {
		public TerminalNode GA() { return getToken(VerilogPreParser.GA, 0); }
		public TerminalNode CELLDEFINE_DIRECTIVE() { return getToken(VerilogPreParser.CELLDEFINE_DIRECTIVE, 0); }
		public Celldefine_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_celldefine_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterCelldefine_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitCelldefine_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitCelldefine_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Celldefine_directiveContext celldefine_directive() throws RecognitionException {
		Celldefine_directiveContext _localctx = new Celldefine_directiveContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_celldefine_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(123);
			match(GA);
			setState(124);
			match(CELLDEFINE_DIRECTIVE);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Default_nettype_directiveContext extends ParserRuleContext {
		public TerminalNode GA() { return getToken(VerilogPreParser.GA, 0); }
		public TerminalNode DEFAULT_NETTYPE_DIRECTIVE() { return getToken(VerilogPreParser.DEFAULT_NETTYPE_DIRECTIVE, 0); }
		public Default_nettype_valueContext default_nettype_value() {
			return getRuleContext(Default_nettype_valueContext.class,0);
		}
		public Default_nettype_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_default_nettype_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterDefault_nettype_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitDefault_nettype_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitDefault_nettype_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Default_nettype_directiveContext default_nettype_directive() throws RecognitionException {
		Default_nettype_directiveContext _localctx = new Default_nettype_directiveContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_default_nettype_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
			match(GA);
			setState(127);
			match(DEFAULT_NETTYPE_DIRECTIVE);
			setState(128);
			default_nettype_value();
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

	@SuppressWarnings("CheckReturnValue")
	public static class Default_nettype_valueContext extends ParserRuleContext {
		public TerminalNode DEFAULT_NETTYPE_VALUE() { return getToken(VerilogPreParser.DEFAULT_NETTYPE_VALUE, 0); }
		public Default_nettype_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_default_nettype_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterDefault_nettype_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitDefault_nettype_value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitDefault_nettype_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Default_nettype_valueContext default_nettype_value() throws RecognitionException {
		Default_nettype_valueContext _localctx = new Default_nettype_valueContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_default_nettype_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(130);
			match(DEFAULT_NETTYPE_VALUE);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Else_directiveContext extends ParserRuleContext {
		public TerminalNode GA() { return getToken(VerilogPreParser.GA, 0); }
		public TerminalNode ELSE_DIRECTIVE() { return getToken(VerilogPreParser.ELSE_DIRECTIVE, 0); }
		public Group_of_linesContext group_of_lines() {
			return getRuleContext(Group_of_linesContext.class,0);
		}
		public Else_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_else_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterElse_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitElse_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitElse_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Else_directiveContext else_directive() throws RecognitionException {
		Else_directiveContext _localctx = new Else_directiveContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_else_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			match(GA);
			setState(133);
			match(ELSE_DIRECTIVE);
			setState(134);
			group_of_lines();
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

	@SuppressWarnings("CheckReturnValue")
	public static class Elsif_directiveContext extends ParserRuleContext {
		public TerminalNode GA() { return getToken(VerilogPreParser.GA, 0); }
		public TerminalNode ELSIF_DIRECTIVE() { return getToken(VerilogPreParser.ELSIF_DIRECTIVE, 0); }
		public Macro_identifierContext macro_identifier() {
			return getRuleContext(Macro_identifierContext.class,0);
		}
		public Group_of_linesContext group_of_lines() {
			return getRuleContext(Group_of_linesContext.class,0);
		}
		public Elsif_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elsif_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterElsif_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitElsif_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitElsif_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Elsif_directiveContext elsif_directive() throws RecognitionException {
		Elsif_directiveContext _localctx = new Elsif_directiveContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_elsif_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136);
			match(GA);
			setState(137);
			match(ELSIF_DIRECTIVE);
			setState(138);
			macro_identifier();
			setState(139);
			group_of_lines();
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

	@SuppressWarnings("CheckReturnValue")
	public static class End_keywords_directiveContext extends ParserRuleContext {
		public TerminalNode GA() { return getToken(VerilogPreParser.GA, 0); }
		public TerminalNode END_KEYWORDS_DIRECTIVE() { return getToken(VerilogPreParser.END_KEYWORDS_DIRECTIVE, 0); }
		public End_keywords_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_end_keywords_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterEnd_keywords_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitEnd_keywords_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitEnd_keywords_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final End_keywords_directiveContext end_keywords_directive() throws RecognitionException {
		End_keywords_directiveContext _localctx = new End_keywords_directiveContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_end_keywords_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(141);
			match(GA);
			setState(142);
			match(END_KEYWORDS_DIRECTIVE);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Endcelldefine_directiveContext extends ParserRuleContext {
		public TerminalNode GA() { return getToken(VerilogPreParser.GA, 0); }
		public TerminalNode ENDCELLDEFINE_DIRECTIVE() { return getToken(VerilogPreParser.ENDCELLDEFINE_DIRECTIVE, 0); }
		public Endcelldefine_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_endcelldefine_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterEndcelldefine_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitEndcelldefine_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitEndcelldefine_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Endcelldefine_directiveContext endcelldefine_directive() throws RecognitionException {
		Endcelldefine_directiveContext _localctx = new Endcelldefine_directiveContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_endcelldefine_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(144);
			match(GA);
			setState(145);
			match(ENDCELLDEFINE_DIRECTIVE);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Endif_directiveContext extends ParserRuleContext {
		public TerminalNode GA() { return getToken(VerilogPreParser.GA, 0); }
		public TerminalNode ENDIF_DIRECTIVE() { return getToken(VerilogPreParser.ENDIF_DIRECTIVE, 0); }
		public Endif_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_endif_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterEndif_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitEndif_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitEndif_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Endif_directiveContext endif_directive() throws RecognitionException {
		Endif_directiveContext _localctx = new Endif_directiveContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_endif_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(147);
			match(GA);
			setState(148);
			match(ENDIF_DIRECTIVE);
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

	@SuppressWarnings("CheckReturnValue")
	public static class FilenameContext extends ParserRuleContext {
		public TerminalNode FILENAME() { return getToken(VerilogPreParser.FILENAME, 0); }
		public FilenameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_filename; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterFilename(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitFilename(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitFilename(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FilenameContext filename() throws RecognitionException {
		FilenameContext _localctx = new FilenameContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_filename);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			match(FILENAME);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Group_of_linesContext extends ParserRuleContext {
		public List<Source_text_Context> source_text_() {
			return getRuleContexts(Source_text_Context.class);
		}
		public Source_text_Context source_text_(int i) {
			return getRuleContext(Source_text_Context.class,i);
		}
		public List<Compiler_directiveContext> compiler_directive() {
			return getRuleContexts(Compiler_directiveContext.class);
		}
		public Compiler_directiveContext compiler_directive(int i) {
			return getRuleContext(Compiler_directiveContext.class,i);
		}
		public Group_of_linesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_group_of_lines; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterGroup_of_lines(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitGroup_of_lines(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitGroup_of_lines(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Group_of_linesContext group_of_lines() throws RecognitionException {
		Group_of_linesContext _localctx = new Group_of_linesContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_group_of_lines);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(156);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(154);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case SOURCE_TEXT:
						{
						setState(152);
						source_text_();
						}
						break;
					case GA:
						{
						setState(153);
						compiler_directive();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					} 
				}
				setState(158);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
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

	@SuppressWarnings("CheckReturnValue")
	public static class IdentifierContext extends ParserRuleContext {
		public TerminalNode SIMPLE_IDENTIFIER() { return getToken(VerilogPreParser.SIMPLE_IDENTIFIER, 0); }
		public IdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(159);
			match(SIMPLE_IDENTIFIER);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Ifdef_directiveContext extends ParserRuleContext {
		public TerminalNode GA() { return getToken(VerilogPreParser.GA, 0); }
		public TerminalNode IFDEF_DIRECTIVE() { return getToken(VerilogPreParser.IFDEF_DIRECTIVE, 0); }
		public Macro_identifierContext macro_identifier() {
			return getRuleContext(Macro_identifierContext.class,0);
		}
		public Group_of_linesContext group_of_lines() {
			return getRuleContext(Group_of_linesContext.class,0);
		}
		public Endif_directiveContext endif_directive() {
			return getRuleContext(Endif_directiveContext.class,0);
		}
		public List<Elsif_directiveContext> elsif_directive() {
			return getRuleContexts(Elsif_directiveContext.class);
		}
		public Elsif_directiveContext elsif_directive(int i) {
			return getRuleContext(Elsif_directiveContext.class,i);
		}
		public Else_directiveContext else_directive() {
			return getRuleContext(Else_directiveContext.class,0);
		}
		public Ifdef_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifdef_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterIfdef_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitIfdef_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitIfdef_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ifdef_directiveContext ifdef_directive() throws RecognitionException {
		Ifdef_directiveContext _localctx = new Ifdef_directiveContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_ifdef_directive);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(161);
			match(GA);
			setState(162);
			match(IFDEF_DIRECTIVE);
			setState(163);
			macro_identifier();
			setState(164);
			group_of_lines();
			setState(168);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(165);
					elsif_directive();
					}
					} 
				}
				setState(170);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			}
			setState(172);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(171);
				else_directive();
				}
				break;
			}
			setState(174);
			endif_directive();
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

	@SuppressWarnings("CheckReturnValue")
	public static class Ifndef_directiveContext extends ParserRuleContext {
		public TerminalNode GA() { return getToken(VerilogPreParser.GA, 0); }
		public TerminalNode IFNDEF_DIRECTIVE() { return getToken(VerilogPreParser.IFNDEF_DIRECTIVE, 0); }
		public Macro_identifierContext macro_identifier() {
			return getRuleContext(Macro_identifierContext.class,0);
		}
		public Group_of_linesContext group_of_lines() {
			return getRuleContext(Group_of_linesContext.class,0);
		}
		public Endif_directiveContext endif_directive() {
			return getRuleContext(Endif_directiveContext.class,0);
		}
		public List<Elsif_directiveContext> elsif_directive() {
			return getRuleContexts(Elsif_directiveContext.class);
		}
		public Elsif_directiveContext elsif_directive(int i) {
			return getRuleContext(Elsif_directiveContext.class,i);
		}
		public Else_directiveContext else_directive() {
			return getRuleContext(Else_directiveContext.class,0);
		}
		public Ifndef_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifndef_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterIfndef_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitIfndef_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitIfndef_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ifndef_directiveContext ifndef_directive() throws RecognitionException {
		Ifndef_directiveContext _localctx = new Ifndef_directiveContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_ifndef_directive);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(176);
			match(GA);
			setState(177);
			match(IFNDEF_DIRECTIVE);
			setState(178);
			macro_identifier();
			setState(179);
			group_of_lines();
			setState(183);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(180);
					elsif_directive();
					}
					} 
				}
				setState(185);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			setState(187);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				{
				setState(186);
				else_directive();
				}
				break;
			}
			setState(189);
			endif_directive();
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

	@SuppressWarnings("CheckReturnValue")
	public static class Include_directiveContext extends ParserRuleContext {
		public TerminalNode GA() { return getToken(VerilogPreParser.GA, 0); }
		public TerminalNode INCLUDE_DIRECTIVE() { return getToken(VerilogPreParser.INCLUDE_DIRECTIVE, 0); }
		public List<TerminalNode> DQ() { return getTokens(VerilogPreParser.DQ); }
		public TerminalNode DQ(int i) {
			return getToken(VerilogPreParser.DQ, i);
		}
		public FilenameContext filename() {
			return getRuleContext(FilenameContext.class,0);
		}
		public Include_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_include_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterInclude_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitInclude_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitInclude_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Include_directiveContext include_directive() throws RecognitionException {
		Include_directiveContext _localctx = new Include_directiveContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_include_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(191);
			match(GA);
			setState(192);
			match(INCLUDE_DIRECTIVE);
			setState(193);
			match(DQ);
			setState(194);
			filename();
			setState(195);
			match(DQ);
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

	@SuppressWarnings("CheckReturnValue")
	public static class LevelContext extends ParserRuleContext {
		public TerminalNode UNSIGNED_NUMBER() { return getToken(VerilogPreParser.UNSIGNED_NUMBER, 0); }
		public LevelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_level; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterLevel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitLevel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitLevel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LevelContext level() throws RecognitionException {
		LevelContext _localctx = new LevelContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_level);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(197);
			match(UNSIGNED_NUMBER);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Line_directiveContext extends ParserRuleContext {
		public TerminalNode GA() { return getToken(VerilogPreParser.GA, 0); }
		public TerminalNode LINE_DIRECTIVE() { return getToken(VerilogPreParser.LINE_DIRECTIVE, 0); }
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public List<TerminalNode> DQ() { return getTokens(VerilogPreParser.DQ); }
		public TerminalNode DQ(int i) {
			return getToken(VerilogPreParser.DQ, i);
		}
		public FilenameContext filename() {
			return getRuleContext(FilenameContext.class,0);
		}
		public LevelContext level() {
			return getRuleContext(LevelContext.class,0);
		}
		public Line_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_line_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterLine_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitLine_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitLine_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Line_directiveContext line_directive() throws RecognitionException {
		Line_directiveContext _localctx = new Line_directiveContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_line_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(199);
			match(GA);
			setState(200);
			match(LINE_DIRECTIVE);
			setState(201);
			number();
			setState(202);
			match(DQ);
			setState(203);
			filename();
			setState(204);
			match(DQ);
			setState(205);
			level();
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

	@SuppressWarnings("CheckReturnValue")
	public static class Macro_delimiterContext extends ParserRuleContext {
		public TerminalNode MACRO_DELIMITER() { return getToken(VerilogPreParser.MACRO_DELIMITER, 0); }
		public Macro_delimiterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_macro_delimiter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterMacro_delimiter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitMacro_delimiter(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitMacro_delimiter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Macro_delimiterContext macro_delimiter() throws RecognitionException {
		Macro_delimiterContext _localctx = new Macro_delimiterContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_macro_delimiter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(207);
			match(MACRO_DELIMITER);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Macro_esc_newlineContext extends ParserRuleContext {
		public TerminalNode MACRO_ESC_NEWLINE() { return getToken(VerilogPreParser.MACRO_ESC_NEWLINE, 0); }
		public Macro_esc_newlineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_macro_esc_newline; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterMacro_esc_newline(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitMacro_esc_newline(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitMacro_esc_newline(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Macro_esc_newlineContext macro_esc_newline() throws RecognitionException {
		Macro_esc_newlineContext _localctx = new Macro_esc_newlineContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_macro_esc_newline);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(209);
			match(MACRO_ESC_NEWLINE);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Macro_esc_quoteContext extends ParserRuleContext {
		public TerminalNode MACRO_ESC_QUOTE() { return getToken(VerilogPreParser.MACRO_ESC_QUOTE, 0); }
		public Macro_esc_quoteContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_macro_esc_quote; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterMacro_esc_quote(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitMacro_esc_quote(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitMacro_esc_quote(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Macro_esc_quoteContext macro_esc_quote() throws RecognitionException {
		Macro_esc_quoteContext _localctx = new Macro_esc_quoteContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_macro_esc_quote);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(211);
			match(MACRO_ESC_QUOTE);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Macro_identifierContext extends ParserRuleContext {
		public TerminalNode MACRO_IDENTIFIER() { return getToken(VerilogPreParser.MACRO_IDENTIFIER, 0); }
		public Macro_identifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_macro_identifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterMacro_identifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitMacro_identifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitMacro_identifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Macro_identifierContext macro_identifier() throws RecognitionException {
		Macro_identifierContext _localctx = new Macro_identifierContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_macro_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(213);
			match(MACRO_IDENTIFIER);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Macro_nameContext extends ParserRuleContext {
		public TerminalNode MACRO_NAME() { return getToken(VerilogPreParser.MACRO_NAME, 0); }
		public Macro_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_macro_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterMacro_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitMacro_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitMacro_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Macro_nameContext macro_name() throws RecognitionException {
		Macro_nameContext _localctx = new Macro_nameContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_macro_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(215);
			match(MACRO_NAME);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Macro_quoteContext extends ParserRuleContext {
		public TerminalNode MACRO_QUOTE() { return getToken(VerilogPreParser.MACRO_QUOTE, 0); }
		public Macro_quoteContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_macro_quote; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterMacro_quote(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitMacro_quote(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitMacro_quote(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Macro_quoteContext macro_quote() throws RecognitionException {
		Macro_quoteContext _localctx = new Macro_quoteContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_macro_quote);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(217);
			match(MACRO_QUOTE);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Macro_textContext extends ParserRuleContext {
		public List<TerminalNode> MACRO_TEXT() { return getTokens(VerilogPreParser.MACRO_TEXT); }
		public TerminalNode MACRO_TEXT(int i) {
			return getToken(VerilogPreParser.MACRO_TEXT, i);
		}
		public List<Macro_delimiterContext> macro_delimiter() {
			return getRuleContexts(Macro_delimiterContext.class);
		}
		public Macro_delimiterContext macro_delimiter(int i) {
			return getRuleContext(Macro_delimiterContext.class,i);
		}
		public List<Macro_esc_newlineContext> macro_esc_newline() {
			return getRuleContexts(Macro_esc_newlineContext.class);
		}
		public Macro_esc_newlineContext macro_esc_newline(int i) {
			return getRuleContext(Macro_esc_newlineContext.class,i);
		}
		public List<Macro_esc_quoteContext> macro_esc_quote() {
			return getRuleContexts(Macro_esc_quoteContext.class);
		}
		public Macro_esc_quoteContext macro_esc_quote(int i) {
			return getRuleContext(Macro_esc_quoteContext.class,i);
		}
		public List<Macro_quoteContext> macro_quote() {
			return getRuleContexts(Macro_quoteContext.class);
		}
		public Macro_quoteContext macro_quote(int i) {
			return getRuleContext(Macro_quoteContext.class,i);
		}
		public List<String_Context> string_() {
			return getRuleContexts(String_Context.class);
		}
		public String_Context string_(int i) {
			return getRuleContext(String_Context.class,i);
		}
		public Macro_textContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_macro_text; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterMacro_text(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitMacro_text(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitMacro_text(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Macro_textContext macro_text() throws RecognitionException {
		Macro_textContext _localctx = new Macro_textContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_macro_text);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(227);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la - 198)) & ~0x3f) == 0 && ((1L << (_la - 198)) & 4260607557633L) != 0) {
				{
				setState(225);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case MACRO_TEXT:
					{
					setState(219);
					match(MACRO_TEXT);
					}
					break;
				case MACRO_DELIMITER:
					{
					setState(220);
					macro_delimiter();
					}
					break;
				case MACRO_ESC_NEWLINE:
					{
					setState(221);
					macro_esc_newline();
					}
					break;
				case MACRO_ESC_QUOTE:
					{
					setState(222);
					macro_esc_quote();
					}
					break;
				case MACRO_QUOTE:
					{
					setState(223);
					macro_quote();
					}
					break;
				case STRING:
					{
					setState(224);
					string_();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(229);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Macro_usageContext extends ParserRuleContext {
		public TerminalNode MACRO_USAGE() { return getToken(VerilogPreParser.MACRO_USAGE, 0); }
		public Macro_usageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_macro_usage; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterMacro_usage(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitMacro_usage(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitMacro_usage(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Macro_usageContext macro_usage() throws RecognitionException {
		Macro_usageContext _localctx = new Macro_usageContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_macro_usage);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(230);
			match(MACRO_USAGE);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Nounconnected_drive_directiveContext extends ParserRuleContext {
		public TerminalNode GA() { return getToken(VerilogPreParser.GA, 0); }
		public TerminalNode NOUNCONNECTED_DRIVE_DIRECTIVE() { return getToken(VerilogPreParser.NOUNCONNECTED_DRIVE_DIRECTIVE, 0); }
		public Nounconnected_drive_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nounconnected_drive_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterNounconnected_drive_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitNounconnected_drive_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitNounconnected_drive_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Nounconnected_drive_directiveContext nounconnected_drive_directive() throws RecognitionException {
		Nounconnected_drive_directiveContext _localctx = new Nounconnected_drive_directiveContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_nounconnected_drive_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(232);
			match(GA);
			setState(233);
			match(NOUNCONNECTED_DRIVE_DIRECTIVE);
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

	@SuppressWarnings("CheckReturnValue")
	public static class NumberContext extends ParserRuleContext {
		public TerminalNode UNSIGNED_NUMBER() { return getToken(VerilogPreParser.UNSIGNED_NUMBER, 0); }
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitNumber(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitNumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_number);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			match(UNSIGNED_NUMBER);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Pragma_directiveContext extends ParserRuleContext {
		public TerminalNode GA() { return getToken(VerilogPreParser.GA, 0); }
		public TerminalNode PRAGMA_DIRECTIVE() { return getToken(VerilogPreParser.PRAGMA_DIRECTIVE, 0); }
		public Pragma_nameContext pragma_name() {
			return getRuleContext(Pragma_nameContext.class,0);
		}
		public List<Pragma_expressionContext> pragma_expression() {
			return getRuleContexts(Pragma_expressionContext.class);
		}
		public Pragma_expressionContext pragma_expression(int i) {
			return getRuleContext(Pragma_expressionContext.class,i);
		}
		public List<TerminalNode> CO() { return getTokens(VerilogPreParser.CO); }
		public TerminalNode CO(int i) {
			return getToken(VerilogPreParser.CO, i);
		}
		public Pragma_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pragma_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterPragma_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitPragma_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitPragma_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Pragma_directiveContext pragma_directive() throws RecognitionException {
		Pragma_directiveContext _localctx = new Pragma_directiveContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_pragma_directive);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(237);
			match(GA);
			setState(238);
			match(PRAGMA_DIRECTIVE);
			setState(239);
			pragma_name();
			setState(248);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LP || (((_la - 197)) & ~0x3f) == 0 && ((1L << (_la - 197)) & 11L) != 0) {
				{
				setState(240);
				pragma_expression();
				setState(245);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CO) {
					{
					{
					setState(241);
					match(CO);
					setState(242);
					pragma_expression();
					}
					}
					setState(247);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
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

	@SuppressWarnings("CheckReturnValue")
	public static class Pragma_expressionContext extends ParserRuleContext {
		public Pragma_valueContext pragma_value() {
			return getRuleContext(Pragma_valueContext.class,0);
		}
		public Pragma_keywordContext pragma_keyword() {
			return getRuleContext(Pragma_keywordContext.class,0);
		}
		public TerminalNode EQ() { return getToken(VerilogPreParser.EQ, 0); }
		public Pragma_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pragma_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterPragma_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitPragma_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitPragma_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Pragma_expressionContext pragma_expression() throws RecognitionException {
		Pragma_expressionContext _localctx = new Pragma_expressionContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_pragma_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(253);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				setState(250);
				pragma_keyword();
				setState(251);
				match(EQ);
				}
				break;
			}
			setState(255);
			pragma_value();
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

	@SuppressWarnings("CheckReturnValue")
	public static class Pragma_keywordContext extends ParserRuleContext {
		public TerminalNode SIMPLE_IDENTIFIER() { return getToken(VerilogPreParser.SIMPLE_IDENTIFIER, 0); }
		public Pragma_keywordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pragma_keyword; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterPragma_keyword(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitPragma_keyword(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitPragma_keyword(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Pragma_keywordContext pragma_keyword() throws RecognitionException {
		Pragma_keywordContext _localctx = new Pragma_keywordContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_pragma_keyword);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(257);
			match(SIMPLE_IDENTIFIER);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Pragma_nameContext extends ParserRuleContext {
		public TerminalNode SIMPLE_IDENTIFIER() { return getToken(VerilogPreParser.SIMPLE_IDENTIFIER, 0); }
		public Pragma_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pragma_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterPragma_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitPragma_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitPragma_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Pragma_nameContext pragma_name() throws RecognitionException {
		Pragma_nameContext _localctx = new Pragma_nameContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_pragma_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(259);
			match(SIMPLE_IDENTIFIER);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Pragma_valueContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(VerilogPreParser.LP, 0); }
		public List<Pragma_expressionContext> pragma_expression() {
			return getRuleContexts(Pragma_expressionContext.class);
		}
		public Pragma_expressionContext pragma_expression(int i) {
			return getRuleContext(Pragma_expressionContext.class,i);
		}
		public TerminalNode RP() { return getToken(VerilogPreParser.RP, 0); }
		public List<TerminalNode> CO() { return getTokens(VerilogPreParser.CO); }
		public TerminalNode CO(int i) {
			return getToken(VerilogPreParser.CO, i);
		}
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public String_Context string_() {
			return getRuleContext(String_Context.class,0);
		}
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public Pragma_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pragma_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterPragma_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitPragma_value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitPragma_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Pragma_valueContext pragma_value() throws RecognitionException {
		Pragma_valueContext _localctx = new Pragma_valueContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_pragma_value);
		int _la;
		try {
			setState(275);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LP:
				enterOuterAlt(_localctx, 1);
				{
				setState(261);
				match(LP);
				setState(262);
				pragma_expression();
				setState(267);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CO) {
					{
					{
					setState(263);
					match(CO);
					setState(264);
					pragma_expression();
					}
					}
					setState(269);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(270);
				match(RP);
				}
				break;
			case UNSIGNED_NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(272);
				number();
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 3);
				{
				setState(273);
				string_();
				}
				break;
			case SIMPLE_IDENTIFIER:
				enterOuterAlt(_localctx, 4);
				{
				setState(274);
				identifier();
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

	@SuppressWarnings("CheckReturnValue")
	public static class Resetall_directiveContext extends ParserRuleContext {
		public TerminalNode GA() { return getToken(VerilogPreParser.GA, 0); }
		public TerminalNode RESETALL_DIRECTIVE() { return getToken(VerilogPreParser.RESETALL_DIRECTIVE, 0); }
		public Resetall_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resetall_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterResetall_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitResetall_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitResetall_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Resetall_directiveContext resetall_directive() throws RecognitionException {
		Resetall_directiveContext _localctx = new Resetall_directiveContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_resetall_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(277);
			match(GA);
			setState(278);
			match(RESETALL_DIRECTIVE);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Source_text_Context extends ParserRuleContext {
		public TerminalNode SOURCE_TEXT() { return getToken(VerilogPreParser.SOURCE_TEXT, 0); }
		public Source_text_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_source_text_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterSource_text_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitSource_text_(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitSource_text_(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Source_text_Context source_text_() throws RecognitionException {
		Source_text_Context _localctx = new Source_text_Context(_ctx, getState());
		enterRule(_localctx, 70, RULE_source_text_);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(280);
			match(SOURCE_TEXT);
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

	@SuppressWarnings("CheckReturnValue")
	public static class String_Context extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(VerilogPreParser.STRING, 0); }
		public String_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterString_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitString_(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitString_(this);
			else return visitor.visitChildren(this);
		}
	}

	public final String_Context string_() throws RecognitionException {
		String_Context _localctx = new String_Context(_ctx, getState());
		enterRule(_localctx, 72, RULE_string_);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(282);
			match(STRING);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Text_macro_definitionContext extends ParserRuleContext {
		public TerminalNode GA() { return getToken(VerilogPreParser.GA, 0); }
		public TerminalNode DEFINE_DIRECTIVE() { return getToken(VerilogPreParser.DEFINE_DIRECTIVE, 0); }
		public Macro_nameContext macro_name() {
			return getRuleContext(Macro_nameContext.class,0);
		}
		public Macro_textContext macro_text() {
			return getRuleContext(Macro_textContext.class,0);
		}
		public Text_macro_definitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_text_macro_definition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterText_macro_definition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitText_macro_definition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitText_macro_definition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Text_macro_definitionContext text_macro_definition() throws RecognitionException {
		Text_macro_definitionContext _localctx = new Text_macro_definitionContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_text_macro_definition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(284);
			match(GA);
			setState(285);
			match(DEFINE_DIRECTIVE);
			setState(286);
			macro_name();
			setState(287);
			macro_text();
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

	@SuppressWarnings("CheckReturnValue")
	public static class Text_macro_usageContext extends ParserRuleContext {
		public TerminalNode GA() { return getToken(VerilogPreParser.GA, 0); }
		public Macro_usageContext macro_usage() {
			return getRuleContext(Macro_usageContext.class,0);
		}
		public Text_macro_usageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_text_macro_usage; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterText_macro_usage(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitText_macro_usage(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitText_macro_usage(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Text_macro_usageContext text_macro_usage() throws RecognitionException {
		Text_macro_usageContext _localctx = new Text_macro_usageContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_text_macro_usage);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(289);
			match(GA);
			setState(290);
			macro_usage();
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

	@SuppressWarnings("CheckReturnValue")
	public static class Time_precisionContext extends ParserRuleContext {
		public TerminalNode TIME_VALUE() { return getToken(VerilogPreParser.TIME_VALUE, 0); }
		public TerminalNode TIME_UNIT() { return getToken(VerilogPreParser.TIME_UNIT, 0); }
		public Time_precisionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_time_precision; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterTime_precision(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitTime_precision(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitTime_precision(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Time_precisionContext time_precision() throws RecognitionException {
		Time_precisionContext _localctx = new Time_precisionContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_time_precision);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(292);
			match(TIME_VALUE);
			setState(293);
			match(TIME_UNIT);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Time_unitContext extends ParserRuleContext {
		public TerminalNode TIME_VALUE() { return getToken(VerilogPreParser.TIME_VALUE, 0); }
		public TerminalNode TIME_UNIT() { return getToken(VerilogPreParser.TIME_UNIT, 0); }
		public Time_unitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_time_unit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterTime_unit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitTime_unit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitTime_unit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Time_unitContext time_unit() throws RecognitionException {
		Time_unitContext _localctx = new Time_unitContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_time_unit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(295);
			match(TIME_VALUE);
			setState(296);
			match(TIME_UNIT);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Timescale_directiveContext extends ParserRuleContext {
		public TerminalNode GA() { return getToken(VerilogPreParser.GA, 0); }
		public TerminalNode TIMESCALE_DIRECTIVE() { return getToken(VerilogPreParser.TIMESCALE_DIRECTIVE, 0); }
		public Time_unitContext time_unit() {
			return getRuleContext(Time_unitContext.class,0);
		}
		public TerminalNode SL() { return getToken(VerilogPreParser.SL, 0); }
		public Time_precisionContext time_precision() {
			return getRuleContext(Time_precisionContext.class,0);
		}
		public Timescale_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_timescale_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterTimescale_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitTimescale_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitTimescale_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Timescale_directiveContext timescale_directive() throws RecognitionException {
		Timescale_directiveContext _localctx = new Timescale_directiveContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_timescale_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(298);
			match(GA);
			setState(299);
			match(TIMESCALE_DIRECTIVE);
			setState(300);
			time_unit();
			setState(301);
			match(SL);
			setState(302);
			time_precision();
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

	@SuppressWarnings("CheckReturnValue")
	public static class Unconnected_drive_directiveContext extends ParserRuleContext {
		public TerminalNode GA() { return getToken(VerilogPreParser.GA, 0); }
		public TerminalNode UNCONNECTED_DRIVE_DIRECTIVE() { return getToken(VerilogPreParser.UNCONNECTED_DRIVE_DIRECTIVE, 0); }
		public Unconnected_drive_valueContext unconnected_drive_value() {
			return getRuleContext(Unconnected_drive_valueContext.class,0);
		}
		public Unconnected_drive_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unconnected_drive_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterUnconnected_drive_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitUnconnected_drive_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitUnconnected_drive_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Unconnected_drive_directiveContext unconnected_drive_directive() throws RecognitionException {
		Unconnected_drive_directiveContext _localctx = new Unconnected_drive_directiveContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_unconnected_drive_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(304);
			match(GA);
			setState(305);
			match(UNCONNECTED_DRIVE_DIRECTIVE);
			setState(306);
			unconnected_drive_value();
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

	@SuppressWarnings("CheckReturnValue")
	public static class Unconnected_drive_valueContext extends ParserRuleContext {
		public TerminalNode UNCONNECTED_DRIVE_VALUE() { return getToken(VerilogPreParser.UNCONNECTED_DRIVE_VALUE, 0); }
		public Unconnected_drive_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unconnected_drive_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterUnconnected_drive_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitUnconnected_drive_value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitUnconnected_drive_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Unconnected_drive_valueContext unconnected_drive_value() throws RecognitionException {
		Unconnected_drive_valueContext _localctx = new Unconnected_drive_valueContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_unconnected_drive_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(308);
			match(UNCONNECTED_DRIVE_VALUE);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Undef_directiveContext extends ParserRuleContext {
		public TerminalNode GA() { return getToken(VerilogPreParser.GA, 0); }
		public TerminalNode UNDEF_DIRECTIVE() { return getToken(VerilogPreParser.UNDEF_DIRECTIVE, 0); }
		public Macro_identifierContext macro_identifier() {
			return getRuleContext(Macro_identifierContext.class,0);
		}
		public Undef_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_undef_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterUndef_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitUndef_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitUndef_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Undef_directiveContext undef_directive() throws RecognitionException {
		Undef_directiveContext _localctx = new Undef_directiveContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_undef_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(310);
			match(GA);
			setState(311);
			match(UNDEF_DIRECTIVE);
			setState(312);
			macro_identifier();
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

	@SuppressWarnings("CheckReturnValue")
	public static class Version_specifierContext extends ParserRuleContext {
		public TerminalNode VERSION_SPECIFIER() { return getToken(VerilogPreParser.VERSION_SPECIFIER, 0); }
		public Version_specifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_version_specifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).enterVersion_specifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VerilogPreParserListener ) ((VerilogPreParserListener)listener).exitVersion_specifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VerilogPreParserVisitor ) return ((VerilogPreParserVisitor<? extends T>)visitor).visitVersion_specifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Version_specifierContext version_specifier() throws RecognitionException {
		Version_specifierContext _localctx = new Version_specifierContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_version_specifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(314);
			match(VERSION_SPECIFIER);
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
		"\u0004\u0001\u00f4\u013d\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007"+
		"\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007"+
		"\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007"+
		"\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007"+
		"\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007"+
		"\u001e\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007"+
		"\"\u0002#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007"+
		"\'\u0002(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007"+
		",\u0002-\u0007-\u0001\u0000\u0005\u0000^\b\u0000\n\u0000\f\u0000a\t\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001"+
		"t\b\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\n\u0001"+
		"\n\u0001\n\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0005\f\u009b\b\f\n"+
		"\f\f\f\u009e\t\f\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0005\u000e\u00a7\b\u000e\n\u000e\f\u000e\u00aa\t\u000e"+
		"\u0001\u000e\u0003\u000e\u00ad\b\u000e\u0001\u000e\u0001\u000e\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0005\u000f\u00b6\b\u000f"+
		"\n\u000f\f\u000f\u00b9\t\u000f\u0001\u000f\u0003\u000f\u00bc\b\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001"+
		"\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001"+
		"\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0005"+
		"\u0019\u00e2\b\u0019\n\u0019\f\u0019\u00e5\t\u0019\u0001\u001a\u0001\u001a"+
		"\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001d"+
		"\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0005\u001d"+
		"\u00f4\b\u001d\n\u001d\f\u001d\u00f7\t\u001d\u0003\u001d\u00f9\b\u001d"+
		"\u0001\u001e\u0001\u001e\u0001\u001e\u0003\u001e\u00fe\b\u001e\u0001\u001e"+
		"\u0001\u001e\u0001\u001f\u0001\u001f\u0001 \u0001 \u0001!\u0001!\u0001"+
		"!\u0001!\u0005!\u010a\b!\n!\f!\u010d\t!\u0001!\u0001!\u0001!\u0001!\u0001"+
		"!\u0003!\u0114\b!\u0001\"\u0001\"\u0001\"\u0001#\u0001#\u0001$\u0001$"+
		"\u0001%\u0001%\u0001%\u0001%\u0001%\u0001&\u0001&\u0001&\u0001\'\u0001"+
		"\'\u0001\'\u0001(\u0001(\u0001(\u0001)\u0001)\u0001)\u0001)\u0001)\u0001"+
		")\u0001*\u0001*\u0001*\u0001*\u0001+\u0001+\u0001,\u0001,\u0001,\u0001"+
		",\u0001-\u0001-\u0001-\u0000\u0000.\u0000\u0002\u0004\u0006\b\n\f\u000e"+
		"\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDF"+
		"HJLNPRTVXZ\u0000\u0000\u0132\u0000_\u0001\u0000\u0000\u0000\u0002s\u0001"+
		"\u0000\u0000\u0000\u0004u\u0001\u0000\u0000\u0000\u0006{\u0001\u0000\u0000"+
		"\u0000\b~\u0001\u0000\u0000\u0000\n\u0082\u0001\u0000\u0000\u0000\f\u0084"+
		"\u0001\u0000\u0000\u0000\u000e\u0088\u0001\u0000\u0000\u0000\u0010\u008d"+
		"\u0001\u0000\u0000\u0000\u0012\u0090\u0001\u0000\u0000\u0000\u0014\u0093"+
		"\u0001\u0000\u0000\u0000\u0016\u0096\u0001\u0000\u0000\u0000\u0018\u009c"+
		"\u0001\u0000\u0000\u0000\u001a\u009f\u0001\u0000\u0000\u0000\u001c\u00a1"+
		"\u0001\u0000\u0000\u0000\u001e\u00b0\u0001\u0000\u0000\u0000 \u00bf\u0001"+
		"\u0000\u0000\u0000\"\u00c5\u0001\u0000\u0000\u0000$\u00c7\u0001\u0000"+
		"\u0000\u0000&\u00cf\u0001\u0000\u0000\u0000(\u00d1\u0001\u0000\u0000\u0000"+
		"*\u00d3\u0001\u0000\u0000\u0000,\u00d5\u0001\u0000\u0000\u0000.\u00d7"+
		"\u0001\u0000\u0000\u00000\u00d9\u0001\u0000\u0000\u00002\u00e3\u0001\u0000"+
		"\u0000\u00004\u00e6\u0001\u0000\u0000\u00006\u00e8\u0001\u0000\u0000\u0000"+
		"8\u00eb\u0001\u0000\u0000\u0000:\u00ed\u0001\u0000\u0000\u0000<\u00fd"+
		"\u0001\u0000\u0000\u0000>\u0101\u0001\u0000\u0000\u0000@\u0103\u0001\u0000"+
		"\u0000\u0000B\u0113\u0001\u0000\u0000\u0000D\u0115\u0001\u0000\u0000\u0000"+
		"F\u0118\u0001\u0000\u0000\u0000H\u011a\u0001\u0000\u0000\u0000J\u011c"+
		"\u0001\u0000\u0000\u0000L\u0121\u0001\u0000\u0000\u0000N\u0124\u0001\u0000"+
		"\u0000\u0000P\u0127\u0001\u0000\u0000\u0000R\u012a\u0001\u0000\u0000\u0000"+
		"T\u0130\u0001\u0000\u0000\u0000V\u0134\u0001\u0000\u0000\u0000X\u0136"+
		"\u0001\u0000\u0000\u0000Z\u013a\u0001\u0000\u0000\u0000\\^\u0003\u0002"+
		"\u0001\u0000]\\\u0001\u0000\u0000\u0000^a\u0001\u0000\u0000\u0000_]\u0001"+
		"\u0000\u0000\u0000_`\u0001\u0000\u0000\u0000`\u0001\u0001\u0000\u0000"+
		"\u0000a_\u0001\u0000\u0000\u0000bt\u0003\u0004\u0002\u0000ct\u0003\u0006"+
		"\u0003\u0000dt\u0003\b\u0004\u0000et\u0003\u0010\b\u0000ft\u0003\u0012"+
		"\t\u0000gt\u0003\u001c\u000e\u0000ht\u0003\u001e\u000f\u0000it\u0003 "+
		"\u0010\u0000jt\u0003$\u0012\u0000kt\u00036\u001b\u0000lt\u0003:\u001d"+
		"\u0000mt\u0003D\"\u0000nt\u0003J%\u0000ot\u0003L&\u0000pt\u0003R)\u0000"+
		"qt\u0003T*\u0000rt\u0003X,\u0000sb\u0001\u0000\u0000\u0000sc\u0001\u0000"+
		"\u0000\u0000sd\u0001\u0000\u0000\u0000se\u0001\u0000\u0000\u0000sf\u0001"+
		"\u0000\u0000\u0000sg\u0001\u0000\u0000\u0000sh\u0001\u0000\u0000\u0000"+
		"si\u0001\u0000\u0000\u0000sj\u0001\u0000\u0000\u0000sk\u0001\u0000\u0000"+
		"\u0000sl\u0001\u0000\u0000\u0000sm\u0001\u0000\u0000\u0000sn\u0001\u0000"+
		"\u0000\u0000so\u0001\u0000\u0000\u0000sp\u0001\u0000\u0000\u0000sq\u0001"+
		"\u0000\u0000\u0000sr\u0001\u0000\u0000\u0000t\u0003\u0001\u0000\u0000"+
		"\u0000uv\u0005G\u0000\u0000vw\u0005\u00d3\u0000\u0000wx\u0005,\u0000\u0000"+
		"xy\u0003Z-\u0000yz\u0005,\u0000\u0000z\u0005\u0001\u0000\u0000\u0000{"+
		"|\u0005G\u0000\u0000|}\u0005\u00d4\u0000\u0000}\u0007\u0001\u0000\u0000"+
		"\u0000~\u007f\u0005G\u0000\u0000\u007f\u0080\u0005\u00d5\u0000\u0000\u0080"+
		"\u0081\u0003\n\u0005\u0000\u0081\t\u0001\u0000\u0000\u0000\u0082\u0083"+
		"\u0005\u00e8\u0000\u0000\u0083\u000b\u0001\u0000\u0000\u0000\u0084\u0085"+
		"\u0005G\u0000\u0000\u0085\u0086\u0005\u00d7\u0000\u0000\u0086\u0087\u0003"+
		"\u0018\f\u0000\u0087\r\u0001\u0000\u0000\u0000\u0088\u0089\u0005G\u0000"+
		"\u0000\u0089\u008a\u0005\u00d8\u0000\u0000\u008a\u008b\u0003,\u0016\u0000"+
		"\u008b\u008c\u0003\u0018\f\u0000\u008c\u000f\u0001\u0000\u0000\u0000\u008d"+
		"\u008e\u0005G\u0000\u0000\u008e\u008f\u0005\u00d9\u0000\u0000\u008f\u0011"+
		"\u0001\u0000\u0000\u0000\u0090\u0091\u0005G\u0000\u0000\u0091\u0092\u0005"+
		"\u00da\u0000\u0000\u0092\u0013\u0001\u0000\u0000\u0000\u0093\u0094\u0005"+
		"G\u0000\u0000\u0094\u0095\u0005\u00db\u0000\u0000\u0095\u0015\u0001\u0000"+
		"\u0000\u0000\u0096\u0097\u0005\u00ea\u0000\u0000\u0097\u0017\u0001\u0000"+
		"\u0000\u0000\u0098\u009b\u0003F#\u0000\u0099\u009b\u0003\u0002\u0001\u0000"+
		"\u009a\u0098\u0001\u0000\u0000\u0000\u009a\u0099\u0001\u0000\u0000\u0000"+
		"\u009b\u009e\u0001\u0000\u0000\u0000\u009c\u009a\u0001\u0000\u0000\u0000"+
		"\u009c\u009d\u0001\u0000\u0000\u0000\u009d\u0019\u0001\u0000\u0000\u0000"+
		"\u009e\u009c\u0001\u0000\u0000\u0000\u009f\u00a0\u0005\u00c5\u0000\u0000"+
		"\u00a0\u001b\u0001\u0000\u0000\u0000\u00a1\u00a2\u0005G\u0000\u0000\u00a2"+
		"\u00a3\u0005\u00dc\u0000\u0000\u00a3\u00a4\u0003,\u0016\u0000\u00a4\u00a8"+
		"\u0003\u0018\f\u0000\u00a5\u00a7\u0003\u000e\u0007\u0000\u00a6\u00a5\u0001"+
		"\u0000\u0000\u0000\u00a7\u00aa\u0001\u0000\u0000\u0000\u00a8\u00a6\u0001"+
		"\u0000\u0000\u0000\u00a8\u00a9\u0001\u0000\u0000\u0000\u00a9\u00ac\u0001"+
		"\u0000\u0000\u0000\u00aa\u00a8\u0001\u0000\u0000\u0000\u00ab\u00ad\u0003"+
		"\f\u0006\u0000\u00ac\u00ab\u0001\u0000\u0000\u0000\u00ac\u00ad\u0001\u0000"+
		"\u0000\u0000\u00ad\u00ae\u0001\u0000\u0000\u0000\u00ae\u00af\u0003\u0014"+
		"\n\u0000\u00af\u001d\u0001\u0000\u0000\u0000\u00b0\u00b1\u0005G\u0000"+
		"\u0000\u00b1\u00b2\u0005\u00dd\u0000\u0000\u00b2\u00b3\u0003,\u0016\u0000"+
		"\u00b3\u00b7\u0003\u0018\f\u0000\u00b4\u00b6\u0003\u000e\u0007\u0000\u00b5"+
		"\u00b4\u0001\u0000\u0000\u0000\u00b6\u00b9\u0001\u0000\u0000\u0000\u00b7"+
		"\u00b5\u0001\u0000\u0000\u0000\u00b7\u00b8\u0001\u0000\u0000\u0000\u00b8"+
		"\u00bb\u0001\u0000\u0000\u0000\u00b9\u00b7\u0001\u0000\u0000\u0000\u00ba"+
		"\u00bc\u0003\f\u0006\u0000\u00bb\u00ba\u0001\u0000\u0000\u0000\u00bb\u00bc"+
		"\u0001\u0000\u0000\u0000\u00bc\u00bd\u0001\u0000\u0000\u0000\u00bd\u00be"+
		"\u0003\u0014\n\u0000\u00be\u001f\u0001\u0000\u0000\u0000\u00bf\u00c0\u0005"+
		"G\u0000\u0000\u00c0\u00c1\u0005\u00de\u0000\u0000\u00c1\u00c2\u0005,\u0000"+
		"\u0000\u00c2\u00c3\u0003\u0016\u000b\u0000\u00c3\u00c4\u0005,\u0000\u0000"+
		"\u00c4!\u0001\u0000\u0000\u0000\u00c5\u00c6\u0005\u00c8\u0000\u0000\u00c6"+
		"#\u0001\u0000\u0000\u0000\u00c7\u00c8\u0005G\u0000\u0000\u00c8\u00c9\u0005"+
		"\u00df\u0000\u0000\u00c9\u00ca\u00038\u001c\u0000\u00ca\u00cb\u0005,\u0000"+
		"\u0000\u00cb\u00cc\u0003\u0016\u000b\u0000\u00cc\u00cd\u0005,\u0000\u0000"+
		"\u00cd\u00ce\u0003\"\u0011\u0000\u00ce%\u0001\u0000\u0000\u0000\u00cf"+
		"\u00d0\u0005\u00eb\u0000\u0000\u00d0\'\u0001\u0000\u0000\u0000\u00d1\u00d2"+
		"\u0005\u00ec\u0000\u0000\u00d2)\u0001\u0000\u0000\u0000\u00d3\u00d4\u0005"+
		"\u00ed\u0000\u0000\u00d4+\u0001\u0000\u0000\u0000\u00d5\u00d6\u0005\u00f4"+
		"\u0000\u0000\u00d6-\u0001\u0000\u0000\u0000\u00d7\u00d8\u0005\u00e9\u0000"+
		"\u0000\u00d8/\u0001\u0000\u0000\u0000\u00d9\u00da\u0005\u00ee\u0000\u0000"+
		"\u00da1\u0001\u0000\u0000\u0000\u00db\u00e2\u0005\u00ef\u0000\u0000\u00dc"+
		"\u00e2\u0003&\u0013\u0000\u00dd\u00e2\u0003(\u0014\u0000\u00de\u00e2\u0003"+
		"*\u0015\u0000\u00df\u00e2\u00030\u0018\u0000\u00e0\u00e2\u0003H$\u0000"+
		"\u00e1\u00db\u0001\u0000\u0000\u0000\u00e1\u00dc\u0001\u0000\u0000\u0000"+
		"\u00e1\u00dd\u0001\u0000\u0000\u0000\u00e1\u00de\u0001\u0000\u0000\u0000"+
		"\u00e1\u00df\u0001\u0000\u0000\u0000\u00e1\u00e0\u0001\u0000\u0000\u0000"+
		"\u00e2\u00e5\u0001\u0000\u0000\u0000\u00e3\u00e1\u0001\u0000\u0000\u0000"+
		"\u00e3\u00e4\u0001\u0000\u0000\u0000\u00e43\u0001\u0000\u0000\u0000\u00e5"+
		"\u00e3\u0001\u0000\u0000\u0000\u00e6\u00e7\u0005\u00e6\u0000\u0000\u00e7"+
		"5\u0001\u0000\u0000\u0000\u00e8\u00e9\u0005G\u0000\u0000\u00e9\u00ea\u0005"+
		"\u00e0\u0000\u0000\u00ea7\u0001\u0000\u0000\u0000\u00eb\u00ec\u0005\u00c8"+
		"\u0000\u0000\u00ec9\u0001\u0000\u0000\u0000\u00ed\u00ee\u0005G\u0000\u0000"+
		"\u00ee\u00ef\u0005\u00e1\u0000\u0000\u00ef\u00f8\u0003@ \u0000\u00f0\u00f5"+
		"\u0003<\u001e\u0000\u00f1\u00f2\u0005\u0018\u0000\u0000\u00f2\u00f4\u0003"+
		"<\u001e\u0000\u00f3\u00f1\u0001\u0000\u0000\u0000\u00f4\u00f7\u0001\u0000"+
		"\u0000\u0000\u00f5\u00f3\u0001\u0000\u0000\u0000\u00f5\u00f6\u0001\u0000"+
		"\u0000\u0000\u00f6\u00f9\u0001\u0000\u0000\u0000\u00f7\u00f5\u0001\u0000"+
		"\u0000\u0000\u00f8\u00f0\u0001\u0000\u0000\u0000\u00f8\u00f9\u0001\u0000"+
		"\u0000\u0000\u00f9;\u0001\u0000\u0000\u0000\u00fa\u00fb\u0003>\u001f\u0000"+
		"\u00fb\u00fc\u0005=\u0000\u0000\u00fc\u00fe\u0001\u0000\u0000\u0000\u00fd"+
		"\u00fa\u0001\u0000\u0000\u0000\u00fd\u00fe\u0001\u0000\u0000\u0000\u00fe"+
		"\u00ff\u0001\u0000\u0000\u0000\u00ff\u0100\u0003B!\u0000\u0100=\u0001"+
		"\u0000\u0000\u0000\u0101\u0102\u0005\u00c5\u0000\u0000\u0102?\u0001\u0000"+
		"\u0000\u0000\u0103\u0104\u0005\u00c5\u0000\u0000\u0104A\u0001\u0000\u0000"+
		"\u0000\u0105\u0106\u0005`\u0000\u0000\u0106\u010b\u0003<\u001e\u0000\u0107"+
		"\u0108\u0005\u0018\u0000\u0000\u0108\u010a\u0003<\u001e\u0000\u0109\u0107"+
		"\u0001\u0000\u0000\u0000\u010a\u010d\u0001\u0000\u0000\u0000\u010b\u0109"+
		"\u0001\u0000\u0000\u0000\u010b\u010c\u0001\u0000\u0000\u0000\u010c\u010e"+
		"\u0001\u0000\u0000\u0000\u010d\u010b\u0001\u0000\u0000\u0000\u010e\u010f"+
		"\u0005\u008e\u0000\u0000\u010f\u0114\u0001\u0000\u0000\u0000\u0110\u0114"+
		"\u00038\u001c\u0000\u0111\u0114\u0003H$\u0000\u0112\u0114\u0003\u001a"+
		"\r\u0000\u0113\u0105\u0001\u0000\u0000\u0000\u0113\u0110\u0001\u0000\u0000"+
		"\u0000\u0113\u0111\u0001\u0000\u0000\u0000\u0113\u0112\u0001\u0000\u0000"+
		"\u0000\u0114C\u0001\u0000\u0000\u0000\u0115\u0116\u0005G\u0000\u0000\u0116"+
		"\u0117\u0005\u00e2\u0000\u0000\u0117E\u0001\u0000\u0000\u0000\u0118\u0119"+
		"\u0005\u00f0\u0000\u0000\u0119G\u0001\u0000\u0000\u0000\u011a\u011b\u0005"+
		"\u00c6\u0000\u0000\u011bI\u0001\u0000\u0000\u0000\u011c\u011d\u0005G\u0000"+
		"\u0000\u011d\u011e\u0005\u00d6\u0000\u0000\u011e\u011f\u0003.\u0017\u0000"+
		"\u011f\u0120\u00032\u0019\u0000\u0120K\u0001\u0000\u0000\u0000\u0121\u0122"+
		"\u0005G\u0000\u0000\u0122\u0123\u00034\u001a\u0000\u0123M\u0001\u0000"+
		"\u0000\u0000\u0124\u0125\u0005\u00f2\u0000\u0000\u0125\u0126\u0005\u00f1"+
		"\u0000\u0000\u0126O\u0001\u0000\u0000\u0000\u0127\u0128\u0005\u00f2\u0000"+
		"\u0000\u0128\u0129\u0005\u00f1\u0000\u0000\u0129Q\u0001\u0000\u0000\u0000"+
		"\u012a\u012b\u0005G\u0000\u0000\u012b\u012c\u0005\u00e3\u0000\u0000\u012c"+
		"\u012d\u0003P(\u0000\u012d\u012e\u0005\u0097\u0000\u0000\u012e\u012f\u0003"+
		"N\'\u0000\u012fS\u0001\u0000\u0000\u0000\u0130\u0131\u0005G\u0000\u0000"+
		"\u0131\u0132\u0005\u00e4\u0000\u0000\u0132\u0133\u0003V+\u0000\u0133U"+
		"\u0001\u0000\u0000\u0000\u0134\u0135\u0005\u00f3\u0000\u0000\u0135W\u0001"+
		"\u0000\u0000\u0000\u0136\u0137\u0005G\u0000\u0000\u0137\u0138\u0005\u00e5"+
		"\u0000\u0000\u0138\u0139\u0003,\u0016\u0000\u0139Y\u0001\u0000\u0000\u0000"+
		"\u013a\u013b\u0005\u00e7\u0000\u0000\u013b[\u0001\u0000\u0000\u0000\u000f"+
		"_s\u009a\u009c\u00a8\u00ac\u00b7\u00bb\u00e1\u00e3\u00f5\u00f8\u00fd\u010b"+
		"\u0113";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}