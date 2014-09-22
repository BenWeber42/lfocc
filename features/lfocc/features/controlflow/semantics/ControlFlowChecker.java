package lfocc.features.controlflow.semantics;

import lfocc.features.controlflow.ast.Conditional;
import lfocc.features.expressions.ast.BooleanType;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

public class ControlFlowChecker extends ASTVisitor {

	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		
		if (node instanceof Conditional) {
			conditional((Conditional) node);
		}
		
		visit(node.getChildren());
	}

	private void conditional(Conditional cond) throws ControlFlowFailure {
		assert cond.getCondition() != null;
		
		if (!(cond.getCondition().getType() instanceof BooleanType)) {
			throw new ControlFlowFailure(String.format(
					"Control flow element requires condition of boolean type ('%s' type given)!",
					cond.getCondition().getType().getName()));
		}
			
	}
	
	@SuppressWarnings("serial")
	public static class ControlFlowFailure extends VisitorFailure {

		public ControlFlowFailure(String message) {
			super(message);
		}
		
	}
}
