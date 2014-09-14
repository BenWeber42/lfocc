package lfocc.features.functions.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.features.expressions.ast.Expression;
import lfocc.framework.compiler.ast.ASTNode;

public class MethodCall extends FunctionCall {

	private Expression receiver;

	public MethodCall(String name, Expression expr, List<Expression> arguments) {
		super(name, arguments);
		this.receiver = expr;
	}

	@Override
	public List<ASTNode> getChildren() {
		List<ASTNode> children = new ArrayList<ASTNode>(arguments);
		children.add(receiver);
		return children;
	}
	
	public Expression getExpr() {
		return receiver;
	}

}
