package lfocc.features.expressions.ast;

import lfocc.framework.compiler.ir.ASTLeafNode;

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

}
