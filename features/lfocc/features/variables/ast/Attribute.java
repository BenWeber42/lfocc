package lfocc.features.variables.ast;

import lfocc.features.expressions.ast.Expression;
import lfocc.features.expressions.ast.UnaryExpression;

public class Attribute extends UnaryExpression {

	private String field;
	
	public Attribute(Expression expr, String field) {
		this.expr = expr;
		this.setField(field);
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}
}
