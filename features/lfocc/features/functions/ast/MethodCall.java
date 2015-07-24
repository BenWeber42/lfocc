package lfocc.features.functions.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.features.expressions.ast.Expression;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTSlot;

public class MethodCall extends FunctionCall {

	private ASTSlot<Expression> receiver;

	public MethodCall(String name, Expression expr, List<Expression> arguments) {
		super(name, arguments);
		this.receiver = new ASTSlot<Expression>(expr);
	}

	@Override
	public List<ASTSlot<? extends ASTNode>> getChildren() {
		List<ASTSlot<? extends ASTNode>> children = new ArrayList<ASTSlot<? extends ASTNode>>(2);
		children.add(arguments);
		children.add(receiver);
		return children;
	}
	
	public Expression getExpr() {
		return receiver.getMember();
	}

}
