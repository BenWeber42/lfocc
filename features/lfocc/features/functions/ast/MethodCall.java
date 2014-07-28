package lfocc.features.functions.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.features.expressions.ast.Expression;
import lfocc.framework.compiler.ast.ASTNode;

public class MethodCall implements Expression {

	private String name;
	private Expression expr;
	private List<Expression> arguments;

	public MethodCall(String name, Expression expr, List<Expression> arguments) {
		this.name = name;
		this.expr = expr;
		this.arguments = arguments;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Expression getExpr() {
		return expr;
	}

	public void setExpr(Expression expr) {
		this.expr = expr;
	}

	public List<Expression> getArguments() {
		return arguments;
	}

	public void setArguments(List<Expression> arguments) {
		this.arguments = arguments;
	}

	@Override
	public List<ASTNode> getChildren() {
		List<ASTNode> children = new ArrayList<ASTNode>(arguments);
		children.add(expr);
		return children;
	}

}
