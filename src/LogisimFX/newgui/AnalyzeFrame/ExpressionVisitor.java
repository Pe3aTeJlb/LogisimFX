/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.AnalyzeFrame;

public interface ExpressionVisitor<T> {

	T visitAnd(Expression a, Expression b);
	T visitOr(Expression a, Expression b);
	T visitXor(Expression a, Expression b);
	T visitNot(Expression a);
	T visitVariable(String name);
	T visitConstant(int value);

}
