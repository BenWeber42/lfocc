package lfocc.features.classes.semantics;

import lfocc.framework.compiler.ast.ASTVisitor.VisitorFailure;

public class ClassTypeFailure extends VisitorFailure {
	private static final long serialVersionUID = -2218012828208014246L;

	public ClassTypeFailure(String message) {
		super(message);
	}
}