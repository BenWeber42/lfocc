package lfocc.features.expressions.ast;

import lfocc.features.types.ast.TypeSymbol;
import lfocc.framework.compiler.ast.ASTLeafNode;

public class FloatConst extends ASTLeafNode implements Expression {
	
	float value;
	
	public FloatConst(float value) {
		this.value = value;
	}

	public FloatConst() {
		value = 0.0f;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	@Override
	public TypeSymbol getType() {
		return new FloatType();
	}

}
