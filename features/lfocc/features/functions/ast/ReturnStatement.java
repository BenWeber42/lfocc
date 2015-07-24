package lfocc.features.functions.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.features.expressions.ast.Expression;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTSlot;
import lfocc.framework.compiler.ast.ExtendableNode;

public class ReturnStatement extends ExtendableNode implements ASTNode {

	private ASTSlot<Expression> expr;
	
	public ReturnStatement(Expression expr) {
		this.expr = new ASTSlot<Expression>(expr);
	}

	public Expression getExpr() {
		return expr.getMember();
	}

	public void setExpr(Expression expr) {
		this.expr.setMember(expr);
	}

	@Override
	public List<ASTSlot<? extends ASTNode>> getChildren() {
		List<ASTSlot<? extends ASTNode>> children = new ArrayList<ASTSlot<? extends ASTNode>>();
		if (expr == null)
			children.add(expr);
		return children;
	}

}
