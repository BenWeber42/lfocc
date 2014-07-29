package lfocc.features.variables.ast;

import lfocc.framework.compiler.ast.ASTLeafNode;

public class VariableDeclaration extends ASTLeafNode {
	
	private String type;
	private String name;
	
	public VariableDeclaration(String type, String name) {
		this.type = type;
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
