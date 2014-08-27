package lfocc.features.classes.ast;

import lfocc.features.expressions.ast.Expression;
import lfocc.framework.compiler.ast.ASTLeafNode;

public class ThisReference extends ASTLeafNode implements Expression {
	
	private ClassType type;
	
	public void setType(ClassType type) {
		this.type = type;
	}

	@Override
	public ClassType getType() {
		return type;
	}

}
