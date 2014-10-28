package lfocc.features.variables.ast;

import lfocc.features.expressions.ast.Expression;
import lfocc.features.types.ast.TypeSymbol;
import lfocc.framework.compiler.ast.ASTLeafNode;

public class Variable extends ASTLeafNode implements Expression {

	private String name;
	private VariableDeclaration declaration;
	
	public Variable(String name) {
		this.name = name;
	}
	
	public Variable(VariableDeclaration declaration) {
		this.name = declaration.getName();
		this.declaration = declaration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public VariableDeclaration getDeclaration() {
		return declaration;
	}

	public void setDeclaration(VariableDeclaration declaration) {
		this.declaration = declaration;
	}
	

	@Override
	public TypeSymbol getType() {
		assert declaration != null;
		return declaration.getType();
	}
}
