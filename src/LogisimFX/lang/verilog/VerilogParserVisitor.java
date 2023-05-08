/*
 * This file is part of LogisimFX. Copyright (c) 2023, Pplos Studio
 * License information is located in the Launch file
 */

// Generated from java-escape by ANTLR 4.11.1

package LogisimFX.lang.verilog;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link VerilogParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface VerilogParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link VerilogParser#library_text}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLibrary_text(VerilogParser.Library_textContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#library_description}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLibrary_description(VerilogParser.Library_descriptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#library_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLibrary_declaration(VerilogParser.Library_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#library_incdir}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLibrary_incdir(VerilogParser.Library_incdirContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#include_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInclude_statement(VerilogParser.Include_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#file_path_spec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile_path_spec(VerilogParser.File_path_specContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#source_text}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSource_text(VerilogParser.Source_textContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#description}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDescription(VerilogParser.DescriptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#module_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModule_declaration(VerilogParser.Module_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#module_keyword}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModule_keyword(VerilogParser.Module_keywordContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#module_parameter_port_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModule_parameter_port_list(VerilogParser.Module_parameter_port_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#list_of_port_declarations}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_of_port_declarations(VerilogParser.List_of_port_declarationsContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#port}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPort(VerilogParser.PortContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#port_implicit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPort_implicit(VerilogParser.Port_implicitContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#port_explicit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPort_explicit(VerilogParser.Port_explicitContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#port_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPort_expression(VerilogParser.Port_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#port_reference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPort_reference(VerilogParser.Port_referenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#port_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPort_declaration(VerilogParser.Port_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#module_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModule_item(VerilogParser.Module_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#module_or_generate_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModule_or_generate_item(VerilogParser.Module_or_generate_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#module_or_generate_item_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModule_or_generate_item_declaration(VerilogParser.Module_or_generate_item_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#parameter_override}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter_override(VerilogParser.Parameter_overrideContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#config_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConfig_declaration(VerilogParser.Config_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#design_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesign_statement(VerilogParser.Design_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#design_statement_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesign_statement_item(VerilogParser.Design_statement_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#config_rule_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConfig_rule_statement(VerilogParser.Config_rule_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#default_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefault_clause(VerilogParser.Default_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#inst_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInst_clause(VerilogParser.Inst_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#inst_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInst_name(VerilogParser.Inst_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#cell_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCell_clause(VerilogParser.Cell_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#liblist_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiblist_clause(VerilogParser.Liblist_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#use_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUse_clause(VerilogParser.Use_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#local_parameter_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocal_parameter_declaration(VerilogParser.Local_parameter_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#parameter_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter_declaration(VerilogParser.Parameter_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#specparam_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecparam_declaration(VerilogParser.Specparam_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#parameter_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter_type(VerilogParser.Parameter_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#inout_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInout_declaration(VerilogParser.Inout_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#input_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput_declaration(VerilogParser.Input_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#output_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutput_declaration(VerilogParser.Output_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#event_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvent_declaration(VerilogParser.Event_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#integer_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInteger_declaration(VerilogParser.Integer_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#net_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNet_declaration(VerilogParser.Net_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#real_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReal_declaration(VerilogParser.Real_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#realtime_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRealtime_declaration(VerilogParser.Realtime_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#reg_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReg_declaration(VerilogParser.Reg_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#time_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTime_declaration(VerilogParser.Time_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#net_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNet_type(VerilogParser.Net_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#output_variable_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutput_variable_type(VerilogParser.Output_variable_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#real_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReal_type(VerilogParser.Real_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#variable_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable_type(VerilogParser.Variable_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#drive_strength}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDrive_strength(VerilogParser.Drive_strengthContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#strength0}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrength0(VerilogParser.Strength0Context ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#strength1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrength1(VerilogParser.Strength1Context ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#charge_strength}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCharge_strength(VerilogParser.Charge_strengthContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#delay3}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDelay3(VerilogParser.Delay3Context ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#delay2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDelay2(VerilogParser.Delay2Context ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#delay_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDelay_value(VerilogParser.Delay_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#list_of_defparam_assignments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_of_defparam_assignments(VerilogParser.List_of_defparam_assignmentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#list_of_event_identifiers}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_of_event_identifiers(VerilogParser.List_of_event_identifiersContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#event_id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvent_id(VerilogParser.Event_idContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#list_of_net_decl_assignments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_of_net_decl_assignments(VerilogParser.List_of_net_decl_assignmentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#list_of_net_identifiers}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_of_net_identifiers(VerilogParser.List_of_net_identifiersContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#net_id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNet_id(VerilogParser.Net_idContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#list_of_param_assignments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_of_param_assignments(VerilogParser.List_of_param_assignmentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#list_of_port_identifiers}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_of_port_identifiers(VerilogParser.List_of_port_identifiersContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#list_of_real_identifiers}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_of_real_identifiers(VerilogParser.List_of_real_identifiersContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#list_of_specparam_assignments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_of_specparam_assignments(VerilogParser.List_of_specparam_assignmentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#list_of_variable_identifiers}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_of_variable_identifiers(VerilogParser.List_of_variable_identifiersContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#list_of_variable_port_identifiers}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_of_variable_port_identifiers(VerilogParser.List_of_variable_port_identifiersContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#var_port_id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_port_id(VerilogParser.Var_port_idContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#defparam_assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefparam_assignment(VerilogParser.Defparam_assignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#net_decl_assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNet_decl_assignment(VerilogParser.Net_decl_assignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#param_assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam_assignment(VerilogParser.Param_assignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#specparam_assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecparam_assignment(VerilogParser.Specparam_assignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#pulse_control_specparam}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPulse_control_specparam(VerilogParser.Pulse_control_specparamContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#error_limit_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitError_limit_value(VerilogParser.Error_limit_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#reject_limit_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReject_limit_value(VerilogParser.Reject_limit_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#limit_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLimit_value(VerilogParser.Limit_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#dimension}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDimension(VerilogParser.DimensionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#range_}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRange_(VerilogParser.Range_Context ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#function_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_declaration(VerilogParser.Function_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#function_item_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_item_declaration(VerilogParser.Function_item_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#function_port_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_port_list(VerilogParser.Function_port_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#func_port_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunc_port_item(VerilogParser.Func_port_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#function_range_or_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_range_or_type(VerilogParser.Function_range_or_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#task_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTask_declaration(VerilogParser.Task_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#task_item_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTask_item_declaration(VerilogParser.Task_item_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#task_port_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTask_port_list(VerilogParser.Task_port_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#task_port_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTask_port_item(VerilogParser.Task_port_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#tf_input_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTf_input_declaration(VerilogParser.Tf_input_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#tf_output_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTf_output_declaration(VerilogParser.Tf_output_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#tf_inout_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTf_inout_declaration(VerilogParser.Tf_inout_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#task_port_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTask_port_type(VerilogParser.Task_port_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#block_item_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock_item_declaration(VerilogParser.Block_item_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#list_of_block_variable_identifiers}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_of_block_variable_identifiers(VerilogParser.List_of_block_variable_identifiersContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#list_of_block_real_identifiers}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_of_block_real_identifiers(VerilogParser.List_of_block_real_identifiersContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#block_variable_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock_variable_type(VerilogParser.Block_variable_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#block_real_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock_real_type(VerilogParser.Block_real_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#gate_instantiation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGate_instantiation(VerilogParser.Gate_instantiationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#cmos_switch_instance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCmos_switch_instance(VerilogParser.Cmos_switch_instanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#enable_gate_instance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnable_gate_instance(VerilogParser.Enable_gate_instanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#mos_switch_instance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMos_switch_instance(VerilogParser.Mos_switch_instanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#n_input_gate_instance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitN_input_gate_instance(VerilogParser.N_input_gate_instanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#n_output_gate_instance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitN_output_gate_instance(VerilogParser.N_output_gate_instanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#pass_switch_instance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPass_switch_instance(VerilogParser.Pass_switch_instanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#pass_enable_switch_instance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPass_enable_switch_instance(VerilogParser.Pass_enable_switch_instanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#pull_gate_instance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPull_gate_instance(VerilogParser.Pull_gate_instanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#name_of_gate_instance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName_of_gate_instance(VerilogParser.Name_of_gate_instanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#pulldown_strength}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPulldown_strength(VerilogParser.Pulldown_strengthContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#pullup_strength}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPullup_strength(VerilogParser.Pullup_strengthContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#enable_terminal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnable_terminal(VerilogParser.Enable_terminalContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#inout_terminal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInout_terminal(VerilogParser.Inout_terminalContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#input_terminal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput_terminal(VerilogParser.Input_terminalContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#ncontrol_terminal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNcontrol_terminal(VerilogParser.Ncontrol_terminalContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#output_terminal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutput_terminal(VerilogParser.Output_terminalContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#pcontrol_terminal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPcontrol_terminal(VerilogParser.Pcontrol_terminalContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#cmos_switchtype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCmos_switchtype(VerilogParser.Cmos_switchtypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#enable_gatetype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnable_gatetype(VerilogParser.Enable_gatetypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#mos_switchtype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMos_switchtype(VerilogParser.Mos_switchtypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#n_input_gatetype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitN_input_gatetype(VerilogParser.N_input_gatetypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#n_output_gatetype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitN_output_gatetype(VerilogParser.N_output_gatetypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#pass_en_switchtype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPass_en_switchtype(VerilogParser.Pass_en_switchtypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#pass_switchtype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPass_switchtype(VerilogParser.Pass_switchtypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#module_instantiation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModule_instantiation(VerilogParser.Module_instantiationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#parameter_value_assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter_value_assignment(VerilogParser.Parameter_value_assignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#list_of_parameter_assignments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_of_parameter_assignments(VerilogParser.List_of_parameter_assignmentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#ordered_parameter_assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrdered_parameter_assignment(VerilogParser.Ordered_parameter_assignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#named_parameter_assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamed_parameter_assignment(VerilogParser.Named_parameter_assignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#module_instance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModule_instance(VerilogParser.Module_instanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#name_of_module_instance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName_of_module_instance(VerilogParser.Name_of_module_instanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#list_of_port_connections}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_of_port_connections(VerilogParser.List_of_port_connectionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#ordered_port_connection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrdered_port_connection(VerilogParser.Ordered_port_connectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#named_port_connection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamed_port_connection(VerilogParser.Named_port_connectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#generate_region}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenerate_region(VerilogParser.Generate_regionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#genvar_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenvar_declaration(VerilogParser.Genvar_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#list_of_genvar_identifiers}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_of_genvar_identifiers(VerilogParser.List_of_genvar_identifiersContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#loop_generate_construct}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoop_generate_construct(VerilogParser.Loop_generate_constructContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#genvar_initialization}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenvar_initialization(VerilogParser.Genvar_initializationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#genvar_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenvar_expression(VerilogParser.Genvar_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#genvar_iteration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenvar_iteration(VerilogParser.Genvar_iterationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#conditional_generate_construct}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditional_generate_construct(VerilogParser.Conditional_generate_constructContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#if_generate_construct}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_generate_construct(VerilogParser.If_generate_constructContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#case_generate_construct}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCase_generate_construct(VerilogParser.Case_generate_constructContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#case_generate_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCase_generate_item(VerilogParser.Case_generate_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#generate_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenerate_block(VerilogParser.Generate_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#generate_block_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenerate_block_name(VerilogParser.Generate_block_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#generate_block_or_null}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenerate_block_or_null(VerilogParser.Generate_block_or_nullContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#udp_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUdp_declaration(VerilogParser.Udp_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#udp_port_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUdp_port_list(VerilogParser.Udp_port_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#udp_declaration_port_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUdp_declaration_port_list(VerilogParser.Udp_declaration_port_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#udp_port_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUdp_port_declaration(VerilogParser.Udp_port_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#udp_output_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUdp_output_declaration(VerilogParser.Udp_output_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#udp_input_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUdp_input_declaration(VerilogParser.Udp_input_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#udp_reg_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUdp_reg_declaration(VerilogParser.Udp_reg_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#udp_body}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUdp_body(VerilogParser.Udp_bodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#combinational_body}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCombinational_body(VerilogParser.Combinational_bodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#combinational_entry}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCombinational_entry(VerilogParser.Combinational_entryContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#sequential_body}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSequential_body(VerilogParser.Sequential_bodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#udp_initial_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUdp_initial_statement(VerilogParser.Udp_initial_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#init_val}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInit_val(VerilogParser.Init_valContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#sequential_entry}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSequential_entry(VerilogParser.Sequential_entryContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#seq_input_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSeq_input_list(VerilogParser.Seq_input_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#level_input_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLevel_input_list(VerilogParser.Level_input_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#edge_input_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEdge_input_list(VerilogParser.Edge_input_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#edge_indicator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEdge_indicator(VerilogParser.Edge_indicatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#current_state}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCurrent_state(VerilogParser.Current_stateContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#next_state}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNext_state(VerilogParser.Next_stateContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#output_symbol}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutput_symbol(VerilogParser.Output_symbolContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#level_symbol}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLevel_symbol(VerilogParser.Level_symbolContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#edge_symbol}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEdge_symbol(VerilogParser.Edge_symbolContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#udp_instantiation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUdp_instantiation(VerilogParser.Udp_instantiationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#udp_instance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUdp_instance(VerilogParser.Udp_instanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#name_of_udp_instance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName_of_udp_instance(VerilogParser.Name_of_udp_instanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#continuous_assign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinuous_assign(VerilogParser.Continuous_assignContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#list_of_net_assignments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_of_net_assignments(VerilogParser.List_of_net_assignmentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#net_assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNet_assignment(VerilogParser.Net_assignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#initial_construct}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInitial_construct(VerilogParser.Initial_constructContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#always_construct}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAlways_construct(VerilogParser.Always_constructContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#blocking_assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlocking_assignment(VerilogParser.Blocking_assignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#nonblocking_assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNonblocking_assignment(VerilogParser.Nonblocking_assignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#procedural_continuous_assignments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedural_continuous_assignments(VerilogParser.Procedural_continuous_assignmentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#variable_assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable_assignment(VerilogParser.Variable_assignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#par_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPar_block(VerilogParser.Par_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#block_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock_name(VerilogParser.Block_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#seq_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSeq_block(VerilogParser.Seq_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(VerilogParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#statement_or_null}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement_or_null(VerilogParser.Statement_or_nullContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#function_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_statement(VerilogParser.Function_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#delay_control}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDelay_control(VerilogParser.Delay_controlContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#delay_or_event_control}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDelay_or_event_control(VerilogParser.Delay_or_event_controlContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#disable_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDisable_statement(VerilogParser.Disable_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#event_control}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvent_control(VerilogParser.Event_controlContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#event_trigger}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvent_trigger(VerilogParser.Event_triggerContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#event_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvent_expression(VerilogParser.Event_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#procedural_timing_control}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedural_timing_control(VerilogParser.Procedural_timing_controlContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#procedural_timing_control_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedural_timing_control_statement(VerilogParser.Procedural_timing_control_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#wait_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWait_statement(VerilogParser.Wait_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#conditional_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditional_statement(VerilogParser.Conditional_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#case_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCase_statement(VerilogParser.Case_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#case_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCase_item(VerilogParser.Case_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#loop_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoop_statement(VerilogParser.Loop_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#system_task_enable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSystem_task_enable(VerilogParser.System_task_enableContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#sys_task_en_port_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSys_task_en_port_list(VerilogParser.Sys_task_en_port_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#sys_task_en_port_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSys_task_en_port_item(VerilogParser.Sys_task_en_port_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#task_enable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTask_enable(VerilogParser.Task_enableContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#task_en_port_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTask_en_port_list(VerilogParser.Task_en_port_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#specify_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecify_block(VerilogParser.Specify_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#specify_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecify_item(VerilogParser.Specify_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#pulsestyle_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPulsestyle_declaration(VerilogParser.Pulsestyle_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#showcancelled_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitShowcancelled_declaration(VerilogParser.Showcancelled_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#path_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPath_declaration(VerilogParser.Path_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#simple_path_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimple_path_declaration(VerilogParser.Simple_path_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#parallel_path_description}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParallel_path_description(VerilogParser.Parallel_path_descriptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#full_path_description}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFull_path_description(VerilogParser.Full_path_descriptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#list_of_path_inputs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_of_path_inputs(VerilogParser.List_of_path_inputsContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#list_of_path_outputs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_of_path_outputs(VerilogParser.List_of_path_outputsContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#specify_input_terminal_descriptor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecify_input_terminal_descriptor(VerilogParser.Specify_input_terminal_descriptorContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#specify_output_terminal_descriptor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecify_output_terminal_descriptor(VerilogParser.Specify_output_terminal_descriptorContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#input_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput_identifier(VerilogParser.Input_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#output_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutput_identifier(VerilogParser.Output_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#path_delay_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPath_delay_value(VerilogParser.Path_delay_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#list_of_path_delay_expressions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_of_path_delay_expressions(VerilogParser.List_of_path_delay_expressionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#t_path_delay_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitT_path_delay_expression(VerilogParser.T_path_delay_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#trise_path_delay_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrise_path_delay_expression(VerilogParser.Trise_path_delay_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#tfall_path_delay_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTfall_path_delay_expression(VerilogParser.Tfall_path_delay_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#tz_path_delay_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTz_path_delay_expression(VerilogParser.Tz_path_delay_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#t01_path_delay_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitT01_path_delay_expression(VerilogParser.T01_path_delay_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#t10_path_delay_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitT10_path_delay_expression(VerilogParser.T10_path_delay_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#t0z_path_delay_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitT0z_path_delay_expression(VerilogParser.T0z_path_delay_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#tz1_path_delay_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTz1_path_delay_expression(VerilogParser.Tz1_path_delay_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#t1z_path_delay_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitT1z_path_delay_expression(VerilogParser.T1z_path_delay_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#tz0_path_delay_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTz0_path_delay_expression(VerilogParser.Tz0_path_delay_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#t0x_path_delay_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitT0x_path_delay_expression(VerilogParser.T0x_path_delay_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#tx1_path_delay_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTx1_path_delay_expression(VerilogParser.Tx1_path_delay_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#t1x_path_delay_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitT1x_path_delay_expression(VerilogParser.T1x_path_delay_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#tx0_path_delay_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTx0_path_delay_expression(VerilogParser.Tx0_path_delay_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#txz_path_delay_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTxz_path_delay_expression(VerilogParser.Txz_path_delay_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#tzx_path_delay_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTzx_path_delay_expression(VerilogParser.Tzx_path_delay_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#path_delay_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPath_delay_expression(VerilogParser.Path_delay_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#edge_sensitive_path_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEdge_sensitive_path_declaration(VerilogParser.Edge_sensitive_path_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#parallel_edge_sensitive_path_description}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParallel_edge_sensitive_path_description(VerilogParser.Parallel_edge_sensitive_path_descriptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#full_edge_sensitive_path_description}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFull_edge_sensitive_path_description(VerilogParser.Full_edge_sensitive_path_descriptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#data_source_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_source_expression(VerilogParser.Data_source_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#edge_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEdge_identifier(VerilogParser.Edge_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#state_dependent_path_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitState_dependent_path_declaration(VerilogParser.State_dependent_path_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#polarity_operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPolarity_operator(VerilogParser.Polarity_operatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#system_timing_check}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSystem_timing_check(VerilogParser.System_timing_checkContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#setup_timing_check}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetup_timing_check(VerilogParser.Setup_timing_checkContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#notifier_opt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotifier_opt(VerilogParser.Notifier_optContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#hold_timing_check}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHold_timing_check(VerilogParser.Hold_timing_checkContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#setuphold_timing_check}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetuphold_timing_check(VerilogParser.Setuphold_timing_checkContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#timing_check_opt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTiming_check_opt(VerilogParser.Timing_check_optContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#stamptime_cond_opt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStamptime_cond_opt(VerilogParser.Stamptime_cond_optContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#checktime_cond_opt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChecktime_cond_opt(VerilogParser.Checktime_cond_optContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#delayed_ref_opt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDelayed_ref_opt(VerilogParser.Delayed_ref_optContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#delayed_data_opt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDelayed_data_opt(VerilogParser.Delayed_data_optContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#recovery_timing_check}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecovery_timing_check(VerilogParser.Recovery_timing_checkContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#removal_timing_check}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRemoval_timing_check(VerilogParser.Removal_timing_checkContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#recrem_timing_check}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecrem_timing_check(VerilogParser.Recrem_timing_checkContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#skew_timing_check}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkew_timing_check(VerilogParser.Skew_timing_checkContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#timeskew_timing_check}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeskew_timing_check(VerilogParser.Timeskew_timing_checkContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#skew_timing_check_opt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkew_timing_check_opt(VerilogParser.Skew_timing_check_optContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#event_based_flag_opt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvent_based_flag_opt(VerilogParser.Event_based_flag_optContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#remain_active_flag_opt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRemain_active_flag_opt(VerilogParser.Remain_active_flag_optContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#fullskew_timing_check}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFullskew_timing_check(VerilogParser.Fullskew_timing_checkContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#period_timing_check}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPeriod_timing_check(VerilogParser.Period_timing_checkContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#width_timing_check}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWidth_timing_check(VerilogParser.Width_timing_checkContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#threshold_opt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThreshold_opt(VerilogParser.Threshold_optContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#nochange_timing_check}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNochange_timing_check(VerilogParser.Nochange_timing_checkContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#checktime_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChecktime_condition(VerilogParser.Checktime_conditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#controlled_reference_event}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitControlled_reference_event(VerilogParser.Controlled_reference_eventContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#data_event}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_event(VerilogParser.Data_eventContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#delayed_data}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDelayed_data(VerilogParser.Delayed_dataContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#delayed_reference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDelayed_reference(VerilogParser.Delayed_referenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#end_edge_offset}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnd_edge_offset(VerilogParser.End_edge_offsetContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#event_based_flag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvent_based_flag(VerilogParser.Event_based_flagContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#notifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotifier(VerilogParser.NotifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#reference_event}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReference_event(VerilogParser.Reference_eventContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#remain_active_flag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRemain_active_flag(VerilogParser.Remain_active_flagContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#stamptime_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStamptime_condition(VerilogParser.Stamptime_conditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#start_edge_offset}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStart_edge_offset(VerilogParser.Start_edge_offsetContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#threshold}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThreshold(VerilogParser.ThresholdContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#timing_check_limit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTiming_check_limit(VerilogParser.Timing_check_limitContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#timing_check_event}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTiming_check_event(VerilogParser.Timing_check_eventContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#controlled_timing_check_event}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitControlled_timing_check_event(VerilogParser.Controlled_timing_check_eventContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#timing_check_event_control}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTiming_check_event_control(VerilogParser.Timing_check_event_controlContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#specify_terminal_descriptor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecify_terminal_descriptor(VerilogParser.Specify_terminal_descriptorContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#edge_control_specifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEdge_control_specifier(VerilogParser.Edge_control_specifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#edge_descriptor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEdge_descriptor(VerilogParser.Edge_descriptorContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#timing_check_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTiming_check_condition(VerilogParser.Timing_check_conditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#scalar_timing_check_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScalar_timing_check_condition(VerilogParser.Scalar_timing_check_conditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#scalar_constant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScalar_constant(VerilogParser.Scalar_constantContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#concatenation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConcatenation(VerilogParser.ConcatenationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#constant_concatenation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant_concatenation(VerilogParser.Constant_concatenationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#constant_multiple_concatenation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant_multiple_concatenation(VerilogParser.Constant_multiple_concatenationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#module_path_concatenation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModule_path_concatenation(VerilogParser.Module_path_concatenationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#module_path_multiple_concatenation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModule_path_multiple_concatenation(VerilogParser.Module_path_multiple_concatenationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#multiple_concatenation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiple_concatenation(VerilogParser.Multiple_concatenationContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#constant_function_call}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant_function_call(VerilogParser.Constant_function_callContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#constant_system_function_call}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant_system_function_call(VerilogParser.Constant_system_function_callContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#function_call}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_call(VerilogParser.Function_callContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#system_function_call}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSystem_function_call(VerilogParser.System_function_callContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#sys_func_call_port_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSys_func_call_port_list(VerilogParser.Sys_func_call_port_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#base_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBase_expression(VerilogParser.Base_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#constant_base_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant_base_expression(VerilogParser.Constant_base_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#constant_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant_expression(VerilogParser.Constant_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#constant_mintypmax_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant_mintypmax_expression(VerilogParser.Constant_mintypmax_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#constant_range_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant_range_expression(VerilogParser.Constant_range_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#dimension_constant_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDimension_constant_expression(VerilogParser.Dimension_constant_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(VerilogParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#lsb_constant_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLsb_constant_expression(VerilogParser.Lsb_constant_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#mintypmax_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMintypmax_expression(VerilogParser.Mintypmax_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#module_path_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModule_path_expression(VerilogParser.Module_path_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#module_path_mintypmax_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModule_path_mintypmax_expression(VerilogParser.Module_path_mintypmax_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#msb_constant_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMsb_constant_expression(VerilogParser.Msb_constant_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#range_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRange_expression(VerilogParser.Range_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#width_constant_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWidth_constant_expression(VerilogParser.Width_constant_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#constant_primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant_primary(VerilogParser.Constant_primaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#module_path_primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModule_path_primary(VerilogParser.Module_path_primaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(VerilogParser.PrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#select_}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_(VerilogParser.Select_Context ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#bit_select}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBit_select(VerilogParser.Bit_selectContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#net_lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNet_lvalue(VerilogParser.Net_lvalueContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#const_select}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConst_select(VerilogParser.Const_selectContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#const_bit_select}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConst_bit_select(VerilogParser.Const_bit_selectContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#variable_lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable_lvalue(VerilogParser.Variable_lvalueContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#unary_operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnary_operator(VerilogParser.Unary_operatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#unary_module_path_operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnary_module_path_operator(VerilogParser.Unary_module_path_operatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(VerilogParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#real_number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReal_number(VerilogParser.Real_numberContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#decimal_number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecimal_number(VerilogParser.Decimal_numberContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#binary_number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_number(VerilogParser.Binary_numberContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#octal_number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOctal_number(VerilogParser.Octal_numberContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#hex_number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHex_number(VerilogParser.Hex_numberContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#size}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSize(VerilogParser.SizeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#fixed_point_number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFixed_point_number(VerilogParser.Fixed_point_numberContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#exponential_number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExponential_number(VerilogParser.Exponential_numberContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#unsigned_number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnsigned_number(VerilogParser.Unsigned_numberContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#decimal_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecimal_value(VerilogParser.Decimal_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#binary_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_value(VerilogParser.Binary_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#octal_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOctal_value(VerilogParser.Octal_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#hex_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHex_value(VerilogParser.Hex_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#decimal_base}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecimal_base(VerilogParser.Decimal_baseContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#binary_base}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_base(VerilogParser.Binary_baseContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#octal_base}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOctal_base(VerilogParser.Octal_baseContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#hex_base}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHex_base(VerilogParser.Hex_baseContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#string_}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString_(VerilogParser.String_Context ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#attribute_instance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttribute_instance(VerilogParser.Attribute_instanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#attr_spec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttr_spec(VerilogParser.Attr_specContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#attr_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttr_name(VerilogParser.Attr_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#block_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock_identifier(VerilogParser.Block_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#cell_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCell_identifier(VerilogParser.Cell_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#config_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConfig_identifier(VerilogParser.Config_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#escaped_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEscaped_identifier(VerilogParser.Escaped_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#event_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvent_identifier(VerilogParser.Event_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#function_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_identifier(VerilogParser.Function_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#gate_instance_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGate_instance_identifier(VerilogParser.Gate_instance_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#generate_block_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenerate_block_identifier(VerilogParser.Generate_block_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#genvar_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenvar_identifier(VerilogParser.Genvar_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#hierarchical_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHierarchical_identifier(VerilogParser.Hierarchical_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#hier_ref}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHier_ref(VerilogParser.Hier_refContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(VerilogParser.IdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#input_port_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput_port_identifier(VerilogParser.Input_port_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#instance_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstance_identifier(VerilogParser.Instance_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#library_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLibrary_identifier(VerilogParser.Library_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#module_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModule_identifier(VerilogParser.Module_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#module_instance_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModule_instance_identifier(VerilogParser.Module_instance_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#net_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNet_identifier(VerilogParser.Net_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#output_port_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutput_port_identifier(VerilogParser.Output_port_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#parameter_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter_identifier(VerilogParser.Parameter_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#port_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPort_identifier(VerilogParser.Port_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#real_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReal_identifier(VerilogParser.Real_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#simple_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimple_identifier(VerilogParser.Simple_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#specparam_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecparam_identifier(VerilogParser.Specparam_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#system_function_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSystem_function_identifier(VerilogParser.System_function_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#system_task_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSystem_task_identifier(VerilogParser.System_task_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#task_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTask_identifier(VerilogParser.Task_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#terminal_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerminal_identifier(VerilogParser.Terminal_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#topmodule_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTopmodule_identifier(VerilogParser.Topmodule_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#udp_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUdp_identifier(VerilogParser.Udp_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#udp_instance_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUdp_instance_identifier(VerilogParser.Udp_instance_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VerilogParser#variable_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable_identifier(VerilogParser.Variable_identifierContext ctx);
}