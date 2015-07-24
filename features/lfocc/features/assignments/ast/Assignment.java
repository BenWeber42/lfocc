package lfocc.features.assignments.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.features.expressions.ast.Expression;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTSlot;
import lfocc.framework.compiler.ast.ExtendableNode;

public class Assignment extends ExtendableNode {

	private ASTSlot<Expression> target;
	private ASTSlot<Expression> value;
	
	public Assignment(Expression target, Expression value) {
		this.target = new ASTSlot<Expression>(target);
		this.value = new ASTSlot<Expression>(value);
	}
	
	public Expression getTarget() {
		return target.getMember();
	}

	public void setTarget(Expression target) {
		this.target.setMember(target);
	}

	public Expression getValue() {
		return value.getMember();
	}

	public void setValue(Expression value) {
		this.value.setMember(value);
	}

	@Override
	public List<ASTSlot<? extends ASTNode>> getChildren() {
		ArrayList<ASTSlot<? extends ASTNode>> children = new ArrayList<ASTSlot<? extends ASTNode>>();
		children.add(target);
		children.add(value);
		return children;
	}

}