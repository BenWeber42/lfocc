package lfocc.features.expressions.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ExtendableNode;

public class UnaryExpression extends ExtendableNode implements Expression {

	protected Expression expr;

	@Override
	public List<ASTNode> getChildren() {
		return new ArrayList<ASTNode>(Arrays.asList(expr));
	}

	public Expression getExpr() {
		return expr;
	}

	public void setExpr(Expression expr) {
		this.expr = expr;
	}

}
