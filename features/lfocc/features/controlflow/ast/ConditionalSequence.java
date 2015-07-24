package lfocc.features.controlflow.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTSequence;
import lfocc.framework.compiler.ast.ASTSlot;
import lfocc.framework.compiler.ast.ExtendableNode;

public class ConditionalSequence extends ExtendableNode {

	private ASTSlot<ASTSequence> conditionals = new ASTSlot<ASTSequence>(new ASTSequence());
	private ASTSlot<ElseConditional> _else = null;
	
	public void insert(IfConditional _if) {
		conditionals.getMember().insert(_if);
	}

	public ASTSequence getConditionals() {
		return conditionals.getMember();
	}

	public ElseConditional getElse() {
		if (_else == null)
			return null;
		return _else.getMember();
	}

	public void setElse(ElseConditional _else) {
		this._else.setMember(_else);
	}

	@Override
	public List<ASTSlot<? extends ASTNode>> getChildren() {
		List<ASTSlot<? extends ASTNode>> children = new ArrayList<ASTSlot<? extends ASTNode>>(1);
		children.add(conditionals);
		if (_else != null)
			children.add(_else);
		return children;
	}

}
