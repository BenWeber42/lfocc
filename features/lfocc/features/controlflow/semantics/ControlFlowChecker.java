package lfocc.features.controlflow.semantics;

import lfocc.features.controlflow.ast.Conditional;
import lfocc.features.controlflow.ast.ForLoop;
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

		if (cond instanceof ForLoop && cond.getCondition() == null) {
			return; // for loop is allowed to have an empty condition
		}

		assert cond.getCondition() != null;
		assert cond.getCondition().getType() != null;
		
		if (!(cond.getCondition().getType() instanceof BooleanType)) {
			throw new ControlFlowFailure(String.format(
					"Control flow element requires condition of boolean type ('%s' type given)!",
					cond.getCondition().getType().getName()));
		}
			
	}
	
	public static class ControlFlowFailure extends VisitorFailure {
		private static final long serialVersionUID = -2364390286663999314L;

		public ControlFlowFailure(String message) {
			super(message);
		}
		
	}
}
