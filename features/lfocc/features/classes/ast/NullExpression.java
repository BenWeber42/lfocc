package lfocc.features.classes.ast;

import lfocc.features.expressions.ast.Expression;
import lfocc.features.types.ast.TypeSymbol;
import lfocc.framework.compiler.ast.ASTLeafNode;

public class NullExpression extends ASTLeafNode implements Expression {

	@Override
	public TypeSymbol getType() {
		return new NullType();
	}

}
