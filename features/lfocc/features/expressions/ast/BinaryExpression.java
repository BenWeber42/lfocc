package lfocc.features.expressions.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTSlot;
import lfocc.framework.compiler.ast.ExtendableNode;

public abstract class BinaryExpression extends ExtendableNode implements Expression {

	protected ASTSlot<Expression> left;
	protected ASTSlot<Expression> right;
	
	@Override
	public List<ASTSlot<? extends ASTNode>> getChildren() {
		List<ASTSlot<? extends ASTNode>> children = new ArrayList<ASTSlot<? extends ASTNode>>();
		children.add(left);
		children.add(right);
		return children;
	}

	public Expression getLeft() {
		return left.getMember();
	}

	public void setLeft(Expression left) {
		this.left.setMember(left);
	}

	public Expression getRight() {
		return right.getMember();
	}

	public void setRight(Expression right) {
		this.right.setMember(right);
	}

}
