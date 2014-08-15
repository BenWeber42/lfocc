package lfocc.features.variables.ast;

import lfocc.features.types.ast.TypeSymbol;
import lfocc.framework.compiler.ast.ASTLeafNode;

public class VariableDeclaration extends ASTLeafNode {
	
	private TypeSymbol type;
	private String name;
	
	public VariableDeclaration(TypeSymbol type, String name) {
		this.type = type;
		this.name = name;
	}

	public TypeSymbol getType() {
		return type;
	}

	public void setType(TypeSymbol type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
