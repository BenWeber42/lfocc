package lfocc.features.globalscope.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTSequence;
import lfocc.framework.compiler.ast.ASTSlot;
import lfocc.framework.compiler.ast.ExtendableNode;

public class GlobalScope extends ExtendableNode {
	
	private ASTSlot<ASTSequence> members;
	
	public GlobalScope(List<ASTNode> members) {
		this.members = new ASTSlot<ASTSequence>(new ASTSequence(members));
	}

	public void add(ASTNode node) {
		members.getMember().add(node);
	}
	
	public void addAll(List<ASTNode> nodes) {
		members.getMember().addAll(nodes);
	}

	@Override
	public List<ASTSlot<? extends ASTNode>> getChildren() {
		List<ASTSlot<? extends ASTNode>> children = new ArrayList<ASTSlot<? extends ASTNode>>(1);
		children.add(members);
		return children;
	}
	
}
