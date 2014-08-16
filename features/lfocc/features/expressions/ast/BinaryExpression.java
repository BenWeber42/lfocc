package lfocc.features.expressions.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ExtendableNode;

public abstract class BinaryExpression extends ExtendableNode implements Expression {

	protected Expression left;
	protected Expression right;
	
	@Override
	public List<ASTNode> getChildren() {
		return new ArrayList<ASTNode>(Arrays.asList(left, right));
	}

	public Expression getLeft() {
		return left;
	}

	public void setLeft(Expression left) {
		this.left = left;
	}

	public Expression getRight() {
		return right;
	}

	public void setRight(Expression right) {
		this.right = right;
	}

}
