package lfocc.features.functions.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lfocc.features.expressions.ast.Expression;
import lfocc.framework.compiler.ast.ASTNode;

public class ReturnStatement implements ASTNode {

	private Expression expr;
	
	public ReturnStatement(Expression expr) {
		this.expr = expr;
	}

	public Expression getExpr() {
		return expr;
	}

	public void setExpr(Expression expr) {
		this.expr = expr;
	}

	@Override
	public List<ASTNode> getChildren() {
		if (expr == null)
			return new ArrayList<ASTNode>();
		return new ArrayList<ASTNode>(Arrays.asList(expr));
	}

}
