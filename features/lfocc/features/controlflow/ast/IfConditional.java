package lfocc.features.controlflow.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.features.expressions.ast.Expression;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ExtendableNode;

public class IfConditional extends ExtendableNode implements Conditional {

	private Expression condition;
	private List<ASTNode> code;

	public IfConditional(Expression condition, List<ASTNode> code) {
		this.condition = condition;
		this.code = code;
	}
	
	@Override
	public Expression getCondition() {
		return condition;
	}

	@Override
	public void setCondition(Expression condition) {
		this.condition = condition;
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
		children.add(condition);
		children.addAll(code);
		return children;
	}

}
