package lfocc.features.classes.semantics;

import lfocc.features.assignments.ast.Assignment;
import lfocc.features.assignments.semantics.AssignmentFailure;
import lfocc.features.classes.ast.ClassType;
import lfocc.features.classes.ast.NullType;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

public class ClassAssignmentChecker extends ASTVisitor {

	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		
		if (node instanceof Assignment) {
			assignment((Assignment) node);
		}
		
		super.visit(node);
	}

	private void assignment(Assignment assign) throws AssignmentFailure {
		
		assert !(assign.getTarget().getType() instanceof NullType);
		
		if (!(assign.getTarget().getType() instanceof ClassType))
			return;
		
		if (assign.getValue().getType() instanceof NullType)
			return;
		
		if (!(assign.getValue().getType() instanceof ClassType)) {
			throw new AssignmentFailure(String.format(
					"Can't assign type '%s' to variable of classtype!",
					assign.getValue().getType().getName()));
		}
		
		assert assign.getValue().getType() instanceof ClassType;
		assert assign.getTarget().getType() instanceof ClassType;
			
		if (!((ClassType) assign.getValue().getType()).isParent(
				(ClassType) assign.getTarget().getType()
				)) {
			
			throw new AssignmentFailure(String.format(
					"Can't assign type '%s' to variable of classtype!",
					assign.getValue().getType().getName()));
		}
	}
}
