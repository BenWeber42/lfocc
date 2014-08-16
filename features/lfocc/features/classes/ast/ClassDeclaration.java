package lfocc.features.classes.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.framework.compiler.ast.ASTNode;

public class ClassDeclaration implements ASTNode {
	
	private String name;
	private String parent;
	private List<ASTNode> members;
	
	public ClassDeclaration(String name) {
		this.name = name;
		this.parent = "Object";
		this.members = new ArrayList<ASTNode>();
	}

	public ClassDeclaration(String name, String parent, List<ASTNode> members) {
		this.name = name;
		if (parent != null)
			this.parent = parent;
		else
			this.parent = "Object";
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
