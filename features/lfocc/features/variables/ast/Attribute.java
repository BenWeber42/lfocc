package lfocc.features.variables.ast;

import lfocc.features.expressions.ast.Expression;
import lfocc.features.expressions.ast.UnaryExpression;
import lfocc.features.types.ast.TypeSymbol;

public class Attribute extends UnaryExpression {

	private String field;
	private VariableDeclaration declaration;
	
	public Attribute(Expression expr, String field) {
		setExpr(expr);
		this.setField(field);
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public VariableDeclaration getDeclaration() {
		return declaration;
	}

	public void setDeclaration(VariableDeclaration declaration) {
		this.declaration = declaration;
	}

	@Override
	public TypeSymbol getType() {
		assert declaration != null;
		return declaration.getType();
	}
}
