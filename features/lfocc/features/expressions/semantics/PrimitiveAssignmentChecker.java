package lfocc.features.expressions.semantics;

import lfocc.features.assignments.ast.Assignment;
import lfocc.features.assignments.semantics.AssignmentFailure;
import lfocc.features.expressions.ast.BooleanType;
import lfocc.features.expressions.ast.Expression;
import lfocc.features.expressions.ast.FloatType;
import lfocc.features.expressions.ast.IntType;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

public class PrimitiveAssignmentChecker extends ASTVisitor {
	
	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		if (node instanceof Assignment) {
			assignment((Assignment) node);
		}
		
		super.visit(node);
	}

	private void assignment(Assignment assign) throws AssignmentFailure {
		if (!isPrimitive(assign.getTarget()))
			return;
			
		if (!assign.getTarget().getType().equals(assign.getValue().getType())) {
			throw  new AssignmentFailure(String.format(
					"Can't assign type '%s' to variable of type '%s'!",
					assign.getValue().getType().getName(),
					assign.getTarget().getType().getName()));
		}
	}
	
	private boolean isPrimitive(Expression expr) {
		return expr.getType() instanceof IntType ||
				expr.getType() instanceof FloatType ||
				expr.getType() instanceof BooleanType;
	}

}
