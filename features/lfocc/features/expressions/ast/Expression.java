package lfocc.features.expressions.ast;

import lfocc.features.types.ast.TypeSymbol;
import lfocc.framework.compiler.ast.ASTNode;

/*
 * Many different ast nodes inherit from Expression.
 * Thus if the Expressions feature isn't activated compilation will fail.
 * However, a language without expressions is so incredibly limited, that it's
 * barely useful.
 */
public interface Expression extends ASTNode {
	public TypeSymbol getType();
}
