package lfocc.features.controlflow.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.features.expressions.ast.Expression;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTSequence;
import lfocc.framework.compiler.ast.ASTSlot;
import lfocc.framework.compiler.ast.ExtendableNode;

public class IfConditional extends ExtendableNode implements Conditional {

	private ASTSlot<Expression> condition;
	private ASTSlot<ASTSequence> code;

	public IfConditional(Expression condition, List<ASTNode> code) {
		this.condition = new ASTSlot<Expression>(condition);
		this.code = new ASTSlot<ASTSequence>(new ASTSequence(code));
	}
	
	@Override
	public Expression getCondition() {
		return condition.getMember();
	}

	@Override
	public void setCondition(Expression condition) {
		this.condition.setMember(condition);
	}

	public ASTSequence getCode() {
		return code.getMember();
	}

	public void setCode(ASTSequence code) {
		this.code.setMember(code);
	}

	@Override
	public List<ASTSlot<? extends ASTNode>> getChildren() {
		List<ASTSlot<? extends ASTNode>> children = new ArrayList<ASTSlot<? extends ASTNode>>(2);
		children.add(condition);
		children.add(code);
		return children;
	}

}
