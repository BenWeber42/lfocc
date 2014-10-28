package lfocc.features.globalscope.ast;

import java.util.List;

import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ExtendableNode;

public class GlobalScope extends ExtendableNode {
	
	private List<ASTNode> children;
	
	public GlobalScope(List<ASTNode> children) {
		this.children = children;
	}

	@Override
	public List<ASTNode> getChildren() {
		return children;
	}
	
	public void add(ASTNode node) {
		children.add(node);
	}
	
	public void addAll(List<ASTNode> nodes) {
		children.addAll(nodes);
	}

}
