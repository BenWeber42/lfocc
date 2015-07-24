package lfocc.features.assignments.semantics;

import lfocc.framework.compiler.ast.ASTVisitor.VisitorFailure;

public class AssignmentFailure extends VisitorFailure {

	private static final long serialVersionUID = 7556336808171707195L;

	public AssignmentFailure(String message) {
		super(message);
	}

}
