package lfocc.features.controlflow.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.features.expressions.ast.Expression;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ExtendableNode;

public class ForLoop extends ExtendableNode implements Conditional {

	private List<ASTNode> init;
	private Expression condition;
	private List<ASTNode> repeat;
	private List<ASTNode> code;
	
	public ForLoop(List<ASTNode> init, Expression condition,
			List<ASTNode> repeat, List<ASTNode> code) {
		
		assert code != null;
		this.init = init;
		this.condition = condition;
		this.repeat = repeat;
		this.code = code;
	}

	public List<ASTNode> getInit() {
		return init;
	}

	public void setInit(List<ASTNode> init) {
		this.init = init;
	}

	@Override
	public Expression getCondition() {
		return condition;
	}

	@Override
	public void setCondition(Expression condition) {
		this.condition = condition;
	}

	public List<ASTNode> getRepeat() {
		return repeat;
	}

	public void setRepeat(List<ASTNode> repeat) {
		this.repeat = repeat;
	}

	public List<ASTNode> getCode() {
		return code;
	}

	public void setCode(List<ASTNode> code) {
		this.code = code;
	}

	@Override
	public List<ASTNode> getChildren() {
		ArrayList<ASTNode> children = new ArrayList<ASTNode>();

		if (init != null)
			children.addAll(init);

		if (condition != null)
			children.add(condition);

		children.addAll(code);

		if (repeat != null)
			children.addAll(repeat);

		return children;
	}

}
