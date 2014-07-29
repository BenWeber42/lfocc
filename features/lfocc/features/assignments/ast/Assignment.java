package lfocc.features.assignments.ast;

import java.util.List;

import lfocc.features.expressions.ast.Expression;
import lfocc.framework.compiler.ast.ASTNode;

public class Assignment implements ASTNode {

	private Expression target;
	private Expression value;
	
	public Assignment(Expression target, Expression value) {
		this.target = target;
		this.value = value;
	}
	
	public Expression getTarget() {
		return target;
	}

	public void setTarget(Expression target) {
		this.target = target;
	}

	public Expression getValue() {
		return value;
	}

	public void setValue(Expression value) {
		this.value = value;
	}

	@Override
	public List<ASTNode> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

}
