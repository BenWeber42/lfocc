package lfocc.features.controlflow.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.features.expressions.ast.Expression;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTSequence;
import lfocc.framework.compiler.ast.ASTSlot;
import lfocc.framework.compiler.ast.ExtendableNode;

public class ForLoop extends ExtendableNode implements Conditional {

	private ASTSlot<ASTSequence> init;
	private ASTSlot<Expression> condition;
	private ASTSlot<ASTSequence> repeat;
	private ASTSlot<ASTSequence> code;
	
	public ForLoop(List<ASTNode> init, Expression condition,
			List<ASTNode> repeat, List<ASTNode> code) {
		
		this.init = new ASTSlot<ASTSequence>(new ASTSequence(init));
		this.condition = new ASTSlot<Expression>(condition);
		this.repeat = new ASTSlot<ASTSequence>(new ASTSequence(repeat));
		this.code = new ASTSlot<ASTSequence>(new ASTSequence(code));
	}

	public ASTSequence getInit() {
		return init.getMember();
	}

	public void setInit(ASTSequence init) {
		this.init.setMember(init);
	}

	@Override
	public Expression getCondition() {
		return condition.getMember();
	}

	@Override
	public void setCondition(Expression condition) {
		this.condition.setMember(condition);
	}

	public ASTSequence getRepeat() {
		return repeat.getMember();
	}

	public void setRepeat(ASTSequence repeat) {
		this.repeat.setMember(repeat);
	}

	public ASTSequence getCode() {
		return code.getMember();
	}

	public void setCode(ASTSequence code) {
		this.code.setMember(code);
	}

	@Override
	public List<ASTSlot<? extends ASTNode>> getChildren() {
		List<ASTSlot<? extends ASTNode>> children = new ArrayList<ASTSlot<? extends ASTNode>>(4);
		children.add(init);
		children.add(condition);
		children.add(code);
		children.add(repeat);
		return children;
	}

}
