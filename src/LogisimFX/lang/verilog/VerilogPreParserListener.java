/*
 * This file is part of LogisimFX. Copyright (c) 2023, Pplos Studio
 * License information is located in the Launch file
 */

// Generated from java-escape by ANTLR 4.11.1

package LogisimFX.lang.verilog;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link VerilogPreParser}.
 */
public interface VerilogPreParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#source_text}.
	 * @param ctx the parse tree
	 */
	void enterSource_text(VerilogPreParser.Source_textContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#source_text}.
	 * @param ctx the parse tree
	 */
	void exitSource_text(VerilogPreParser.Source_textContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#compiler_directive}.
	 * @param ctx the parse tree
	 */
	void enterCompiler_directive(VerilogPreParser.Compiler_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#compiler_directive}.
	 * @param ctx the parse tree
	 */
	void exitCompiler_directive(VerilogPreParser.Compiler_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#begin_keywords_directive}.
	 * @param ctx the parse tree
	 */
	void enterBegin_keywords_directive(VerilogPreParser.Begin_keywords_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#begin_keywords_directive}.
	 * @param ctx the parse tree
	 */
	void exitBegin_keywords_directive(VerilogPreParser.Begin_keywords_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#celldefine_directive}.
	 * @param ctx the parse tree
	 */
	void enterCelldefine_directive(VerilogPreParser.Celldefine_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#celldefine_directive}.
	 * @param ctx the parse tree
	 */
	void exitCelldefine_directive(VerilogPreParser.Celldefine_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#default_nettype_directive}.
	 * @param ctx the parse tree
	 */
	void enterDefault_nettype_directive(VerilogPreParser.Default_nettype_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#default_nettype_directive}.
	 * @param ctx the parse tree
	 */
	void exitDefault_nettype_directive(VerilogPreParser.Default_nettype_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#default_nettype_value}.
	 * @param ctx the parse tree
	 */
	void enterDefault_nettype_value(VerilogPreParser.Default_nettype_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#default_nettype_value}.
	 * @param ctx the parse tree
	 */
	void exitDefault_nettype_value(VerilogPreParser.Default_nettype_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#else_directive}.
	 * @param ctx the parse tree
	 */
	void enterElse_directive(VerilogPreParser.Else_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#else_directive}.
	 * @param ctx the parse tree
	 */
	void exitElse_directive(VerilogPreParser.Else_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#elsif_directive}.
	 * @param ctx the parse tree
	 */
	void enterElsif_directive(VerilogPreParser.Elsif_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#elsif_directive}.
	 * @param ctx the parse tree
	 */
	void exitElsif_directive(VerilogPreParser.Elsif_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#end_keywords_directive}.
	 * @param ctx the parse tree
	 */
	void enterEnd_keywords_directive(VerilogPreParser.End_keywords_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#end_keywords_directive}.
	 * @param ctx the parse tree
	 */
	void exitEnd_keywords_directive(VerilogPreParser.End_keywords_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#endcelldefine_directive}.
	 * @param ctx the parse tree
	 */
	void enterEndcelldefine_directive(VerilogPreParser.Endcelldefine_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#endcelldefine_directive}.
	 * @param ctx the parse tree
	 */
	void exitEndcelldefine_directive(VerilogPreParser.Endcelldefine_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#endif_directive}.
	 * @param ctx the parse tree
	 */
	void enterEndif_directive(VerilogPreParser.Endif_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#endif_directive}.
	 * @param ctx the parse tree
	 */
	void exitEndif_directive(VerilogPreParser.Endif_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#filename}.
	 * @param ctx the parse tree
	 */
	void enterFilename(VerilogPreParser.FilenameContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#filename}.
	 * @param ctx the parse tree
	 */
	void exitFilename(VerilogPreParser.FilenameContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#group_of_lines}.
	 * @param ctx the parse tree
	 */
	void enterGroup_of_lines(VerilogPreParser.Group_of_linesContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#group_of_lines}.
	 * @param ctx the parse tree
	 */
	void exitGroup_of_lines(VerilogPreParser.Group_of_linesContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(VerilogPreParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(VerilogPreParser.IdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#ifdef_directive}.
	 * @param ctx the parse tree
	 */
	void enterIfdef_directive(VerilogPreParser.Ifdef_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#ifdef_directive}.
	 * @param ctx the parse tree
	 */
	void exitIfdef_directive(VerilogPreParser.Ifdef_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#ifndef_directive}.
	 * @param ctx the parse tree
	 */
	void enterIfndef_directive(VerilogPreParser.Ifndef_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#ifndef_directive}.
	 * @param ctx the parse tree
	 */
	void exitIfndef_directive(VerilogPreParser.Ifndef_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#include_directive}.
	 * @param ctx the parse tree
	 */
	void enterInclude_directive(VerilogPreParser.Include_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#include_directive}.
	 * @param ctx the parse tree
	 */
	void exitInclude_directive(VerilogPreParser.Include_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#level}.
	 * @param ctx the parse tree
	 */
	void enterLevel(VerilogPreParser.LevelContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#level}.
	 * @param ctx the parse tree
	 */
	void exitLevel(VerilogPreParser.LevelContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#line_directive}.
	 * @param ctx the parse tree
	 */
	void enterLine_directive(VerilogPreParser.Line_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#line_directive}.
	 * @param ctx the parse tree
	 */
	void exitLine_directive(VerilogPreParser.Line_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#macro_delimiter}.
	 * @param ctx the parse tree
	 */
	void enterMacro_delimiter(VerilogPreParser.Macro_delimiterContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#macro_delimiter}.
	 * @param ctx the parse tree
	 */
	void exitMacro_delimiter(VerilogPreParser.Macro_delimiterContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#macro_esc_newline}.
	 * @param ctx the parse tree
	 */
	void enterMacro_esc_newline(VerilogPreParser.Macro_esc_newlineContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#macro_esc_newline}.
	 * @param ctx the parse tree
	 */
	void exitMacro_esc_newline(VerilogPreParser.Macro_esc_newlineContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#macro_esc_quote}.
	 * @param ctx the parse tree
	 */
	void enterMacro_esc_quote(VerilogPreParser.Macro_esc_quoteContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#macro_esc_quote}.
	 * @param ctx the parse tree
	 */
	void exitMacro_esc_quote(VerilogPreParser.Macro_esc_quoteContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#macro_identifier}.
	 * @param ctx the parse tree
	 */
	void enterMacro_identifier(VerilogPreParser.Macro_identifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#macro_identifier}.
	 * @param ctx the parse tree
	 */
	void exitMacro_identifier(VerilogPreParser.Macro_identifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#macro_name}.
	 * @param ctx the parse tree
	 */
	void enterMacro_name(VerilogPreParser.Macro_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#macro_name}.
	 * @param ctx the parse tree
	 */
	void exitMacro_name(VerilogPreParser.Macro_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#macro_quote}.
	 * @param ctx the parse tree
	 */
	void enterMacro_quote(VerilogPreParser.Macro_quoteContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#macro_quote}.
	 * @param ctx the parse tree
	 */
	void exitMacro_quote(VerilogPreParser.Macro_quoteContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#macro_text}.
	 * @param ctx the parse tree
	 */
	void enterMacro_text(VerilogPreParser.Macro_textContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#macro_text}.
	 * @param ctx the parse tree
	 */
	void exitMacro_text(VerilogPreParser.Macro_textContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#macro_usage}.
	 * @param ctx the parse tree
	 */
	void enterMacro_usage(VerilogPreParser.Macro_usageContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#macro_usage}.
	 * @param ctx the parse tree
	 */
	void exitMacro_usage(VerilogPreParser.Macro_usageContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#nounconnected_drive_directive}.
	 * @param ctx the parse tree
	 */
	void enterNounconnected_drive_directive(VerilogPreParser.Nounconnected_drive_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#nounconnected_drive_directive}.
	 * @param ctx the parse tree
	 */
	void exitNounconnected_drive_directive(VerilogPreParser.Nounconnected_drive_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(VerilogPreParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(VerilogPreParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#pragma_directive}.
	 * @param ctx the parse tree
	 */
	void enterPragma_directive(VerilogPreParser.Pragma_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#pragma_directive}.
	 * @param ctx the parse tree
	 */
	void exitPragma_directive(VerilogPreParser.Pragma_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#pragma_expression}.
	 * @param ctx the parse tree
	 */
	void enterPragma_expression(VerilogPreParser.Pragma_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#pragma_expression}.
	 * @param ctx the parse tree
	 */
	void exitPragma_expression(VerilogPreParser.Pragma_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#pragma_keyword}.
	 * @param ctx the parse tree
	 */
	void enterPragma_keyword(VerilogPreParser.Pragma_keywordContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#pragma_keyword}.
	 * @param ctx the parse tree
	 */
	void exitPragma_keyword(VerilogPreParser.Pragma_keywordContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#pragma_name}.
	 * @param ctx the parse tree
	 */
	void enterPragma_name(VerilogPreParser.Pragma_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#pragma_name}.
	 * @param ctx the parse tree
	 */
	void exitPragma_name(VerilogPreParser.Pragma_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#pragma_value}.
	 * @param ctx the parse tree
	 */
	void enterPragma_value(VerilogPreParser.Pragma_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#pragma_value}.
	 * @param ctx the parse tree
	 */
	void exitPragma_value(VerilogPreParser.Pragma_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#resetall_directive}.
	 * @param ctx the parse tree
	 */
	void enterResetall_directive(VerilogPreParser.Resetall_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#resetall_directive}.
	 * @param ctx the parse tree
	 */
	void exitResetall_directive(VerilogPreParser.Resetall_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#source_text_}.
	 * @param ctx the parse tree
	 */
	void enterSource_text_(VerilogPreParser.Source_text_Context ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#source_text_}.
	 * @param ctx the parse tree
	 */
	void exitSource_text_(VerilogPreParser.Source_text_Context ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#string_}.
	 * @param ctx the parse tree
	 */
	void enterString_(VerilogPreParser.String_Context ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#string_}.
	 * @param ctx the parse tree
	 */
	void exitString_(VerilogPreParser.String_Context ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#text_macro_definition}.
	 * @param ctx the parse tree
	 */
	void enterText_macro_definition(VerilogPreParser.Text_macro_definitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#text_macro_definition}.
	 * @param ctx the parse tree
	 */
	void exitText_macro_definition(VerilogPreParser.Text_macro_definitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#text_macro_usage}.
	 * @param ctx the parse tree
	 */
	void enterText_macro_usage(VerilogPreParser.Text_macro_usageContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#text_macro_usage}.
	 * @param ctx the parse tree
	 */
	void exitText_macro_usage(VerilogPreParser.Text_macro_usageContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#time_precision}.
	 * @param ctx the parse tree
	 */
	void enterTime_precision(VerilogPreParser.Time_precisionContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#time_precision}.
	 * @param ctx the parse tree
	 */
	void exitTime_precision(VerilogPreParser.Time_precisionContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#time_unit}.
	 * @param ctx the parse tree
	 */
	void enterTime_unit(VerilogPreParser.Time_unitContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#time_unit}.
	 * @param ctx the parse tree
	 */
	void exitTime_unit(VerilogPreParser.Time_unitContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#timescale_directive}.
	 * @param ctx the parse tree
	 */
	void enterTimescale_directive(VerilogPreParser.Timescale_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#timescale_directive}.
	 * @param ctx the parse tree
	 */
	void exitTimescale_directive(VerilogPreParser.Timescale_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#unconnected_drive_directive}.
	 * @param ctx the parse tree
	 */
	void enterUnconnected_drive_directive(VerilogPreParser.Unconnected_drive_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#unconnected_drive_directive}.
	 * @param ctx the parse tree
	 */
	void exitUnconnected_drive_directive(VerilogPreParser.Unconnected_drive_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#unconnected_drive_value}.
	 * @param ctx the parse tree
	 */
	void enterUnconnected_drive_value(VerilogPreParser.Unconnected_drive_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#unconnected_drive_value}.
	 * @param ctx the parse tree
	 */
	void exitUnconnected_drive_value(VerilogPreParser.Unconnected_drive_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#undef_directive}.
	 * @param ctx the parse tree
	 */
	void enterUndef_directive(VerilogPreParser.Undef_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#undef_directive}.
	 * @param ctx the parse tree
	 */
	void exitUndef_directive(VerilogPreParser.Undef_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link VerilogPreParser#version_specifier}.
	 * @param ctx the parse tree
	 */
	void enterVersion_specifier(VerilogPreParser.Version_specifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link VerilogPreParser#version_specifier}.
	 * @param ctx the parse tree
	 */
	void exitVersion_specifier(VerilogPreParser.Version_specifierContext ctx);
}