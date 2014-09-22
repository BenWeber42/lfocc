package lfocc.features.controlflow.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ExtendableNode;

public class ConditionalSequence extends ExtendableNode {

	private List<IfConditional> conditionals = new ArrayList<IfConditional>();
	private ElseConditional _else = null;
	
	public void insert(IfConditional _if) {
		conditionals.add(0, _if);
	}

	public List<IfConditional> getConditionals() {
		return conditionals;
	}

	public ElseConditional getElse() {
		return _else;
	}

	public void setElse(ElseConditional _else) {
		this._else = _else;
	}

	@Override
	public List<ASTNode> getChildren() {
		List<ASTNode> children = new ArrayList<ASTNode>(conditionals);
		if (_else != null)
			children.add(_else);
		return children;
	}

}
