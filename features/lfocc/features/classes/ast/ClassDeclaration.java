package lfocc.features.classes.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTSequence;
import lfocc.framework.compiler.ast.ASTSlot;
import lfocc.framework.compiler.ast.ExtendableNode;

public class ClassDeclaration extends ExtendableNode {
	
	private String name;
	private String parent;
	private ASTSlot<ASTSequence> members = new ASTSlot<ASTSequence>(new ASTSequence());
	private ClassType type;
	
	public ClassDeclaration(String name) {
		this.name = name;
		this.parent = "Object";
	}

	public ClassDeclaration(String name, String parent, List<ASTNode> members) {
		this.name = name;
		if (parent != null)
			this.parent = parent;
		else
			this.parent = "Object";
		
		this.members.setMember(new ASTSequence(members));
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

	public ASTSequence getMembers() {
		return members.getMember();
	}

	public void setMembers(ASTSequence members) {
		this.members.setMember(members);
	}

	@Override
	public List<ASTSlot<? extends ASTNode>> getChildren() {
		List<ASTSlot<? extends ASTNode>> children = new ArrayList<ASTSlot<? extends ASTNode>>(1);
		children.add(members);
		return children;
	}

	public ClassType getType() {
		return type;
	}

	public void setType(ClassType type) {
		this.type = type;
	}

}
