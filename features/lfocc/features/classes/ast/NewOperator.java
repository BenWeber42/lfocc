package lfocc.features.classes.ast;

import lfocc.features.expressions.ast.Expression;
import lfocc.framework.compiler.ast.ASTLeafNode;

public class NewOperator extends ASTLeafNode implements Expression {

	private String className;
	
	public NewOperator(String className) {
		this.setClassName(className);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
