package lfocc.features.expressions.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTSlot;
import lfocc.framework.compiler.ast.ExtendableNode;

public abstract class UnaryExpression extends ExtendableNode implements Expression {

	protected ASTSlot<Expression> expr;

	@Override
	public List<ASTSlot<? extends ASTNode>> getChildren() {
		List<ASTSlot<? extends ASTNode>> children = new ArrayList<ASTSlot<? extends ASTNode>>(1);
		children.add(expr);
		return children;
	}

	public Expression getExpr() {
		return expr.getMember();
	}

	public void setExpr(Expression expr) {
		this.expr.setMember(expr);
	}

}
