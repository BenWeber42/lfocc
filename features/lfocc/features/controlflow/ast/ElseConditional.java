package lfocc.features.controlflow.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTSlot;
import lfocc.framework.compiler.ast.ASTSequence;
import lfocc.framework.compiler.ast.ExtendableNode;

public class ElseConditional extends ExtendableNode implements ASTNode {
	
	private ASTSlot<ASTSequence> code;

	public ElseConditional(List<ASTNode> code) {
		this.code = new ASTSlot<ASTSequence>(new ASTSequence(code));
	}

	public ASTSequence getCode() {
		return code.getMember();
	}

	public void setCode(ASTSequence code) {
		this.code.setMember(code);
	}

	@Override
	public List<ASTSlot<? extends ASTNode>> getChildren() {
		List<ASTSlot<? extends ASTNode>> children = new ArrayList<ASTSlot<? extends ASTNode>>(1);
		children.add(code);
		return children;
	}

}
