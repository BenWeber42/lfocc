package lfocc.features.expressions.ast;

import lfocc.framework.compiler.ir.ASTLeafNode;

public class IntConst extends ASTLeafNode implements Expression {
	
	private int value;
	
	public IntConst() {
		value = 0;
	}
	
	public IntConst(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}

}
