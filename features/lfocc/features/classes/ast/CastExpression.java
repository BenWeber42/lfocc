package lfocc.features.classes.ast;

import lfocc.features.expressions.ast.Expression;
import lfocc.features.expressions.ast.UnaryExpression;

public class CastExpression extends UnaryExpression {

	private String cast;
	private ClassType type;
	
	public CastExpression(String cast, Expression expr) {
		this.setCast(cast);
		this.expr = expr;
	}

	public String getCast() {
		return cast;
	}

	public void setCast(String cast) {
		this.cast = cast;
	}
	
	public void setType(ClassType type) {
		this.type = type;
	}

	@Override
	public ClassType getType() {
		return type;
	}
}
