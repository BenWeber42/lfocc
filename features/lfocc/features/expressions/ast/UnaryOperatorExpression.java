package lfocc.features.expressions.ast;

import lfocc.features.types.ast.TypeSymbol;

public class UnaryOperatorExpression extends UnaryExpression {

	public static enum Operator {
		NOT,
		PLUS,
		MINUS
	};
	
	private Operator operator;
	private TypeSymbol type;
	
	public UnaryOperatorExpression(Operator operator, Expression expr) {
		this.operator = operator;
		this.expr = expr;
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
