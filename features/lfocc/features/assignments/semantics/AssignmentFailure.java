package lfocc.features.assignments.semantics;

import lfocc.framework.compiler.ast.ASTVisitor.VisitorFailure;

@SuppressWarnings("serial")
public class AssignmentFailure extends VisitorFailure {

	public AssignmentFailure(String message) {
		super(message);
	}

}
