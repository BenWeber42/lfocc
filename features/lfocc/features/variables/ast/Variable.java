package lfocc.features.variables.ast;

import lfocc.features.expressions.ast.Expression;
import lfocc.framework.compiler.ast.ASTLeafNode;

public class Variable extends ASTLeafNode implements Expression {

	private String name;
	
	public Variable(String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
