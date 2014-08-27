package lfocc.features.expressions.ast;

import lfocc.features.types.ast.TypeSymbol;

public class BinaryOperatorExpression extends BinaryExpression {

	public static enum Operator {
		PLUS,
		MINUS,
		TIMES,
		DIVIDE,
		MODULO,
		AND,
		OR,
		EQUAL,
		NOT_EQUAL,
		SMALLER,
		SMALLER_EQUAL,
		GREATER,
		GREATER_EQUAL
	};
	
	private Operator operator;
	private TypeSymbol type;
	
	public BinaryOperatorExpression(Operator operator, Expression left, Expression right) {
		this.left = left;
		this.right = right;
		this.operator = operator;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	
	public void setType(TypeSymbol type) {
		this.type = type;
	}

	@Override
	public TypeSymbol getType() {
		return type;
	}

}
