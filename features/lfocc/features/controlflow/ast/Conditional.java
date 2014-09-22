package lfocc.features.controlflow.ast;

import lfocc.features.expressions.ast.Expression;
import lfocc.framework.compiler.ast.ASTNode;

public interface Conditional extends ASTNode {

	public Expression getCondition();
	public void setCondition(Expression condition);
}
