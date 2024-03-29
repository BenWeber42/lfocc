package lfocc.features.assignments.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lfocc.features.expressions.ast.Expression;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ExtendableNode;

public class Assignment extends ExtendableNode {

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
		return new ArrayList<ASTNode>(Arrays.asList(target, value));
	}

}
