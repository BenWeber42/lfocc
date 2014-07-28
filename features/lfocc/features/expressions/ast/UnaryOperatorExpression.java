package lfocc.features.expressions.ast;

public class UnaryOperatorExpression extends UnaryExpression {

	public static enum Operator {
		NOT,
		PLUS,
		MINUS
	};
	
	private Operator operator;
	
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
}
