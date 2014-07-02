package lfocc.framework.compiler.ir;

import java.util.ArrayList;
import java.util.List;

public class ASTNode {

	protected List<ASTNode> children = new ArrayList<ASTNode>();
	
	public ASTNode(List<ASTNode> children) {
		this.children = children;
	}
	
	public List<ASTNode> getChildren() {
		return children;
	}
	
	public void setChildren(List<ASTNode> children) {
		this.children = children;
	}
}
