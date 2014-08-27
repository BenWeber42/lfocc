package lfocc.features.expressions.ast;

import lfocc.features.types.ast.TypeSymbol;
import lfocc.framework.compiler.ast.ASTLeafNode;

public class BooleanConst extends ASTLeafNode implements Expression {

	boolean value;
	
	public BooleanConst() {
		value = false;
	}

	public BooleanConst(boolean value) {
		this.value = value;
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	@Override
	public TypeSymbol getType() {
		return new BooleanType();
	}
}
