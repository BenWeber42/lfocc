package lfocc.features.classes.ast;

import java.util.List;

import lfocc.framework.compiler.ast.ASTNode;

public class ClassNode implements ASTNode {
	
	private String name;
	private String parent;
	private List<ASTNode> members;
	
	public ClassNode(String name, String parent, List<ASTNode> members) {
		this.name = name;
		this.parent = parent;
		this.members = members;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public List<ASTNode> getMembers() {
		return members;
	}

	public void setMembers(List<ASTNode> members) {
		this.members = members;
	}

	@Override
	public List<ASTNode> getChildren() {
		return members;
	}

}
