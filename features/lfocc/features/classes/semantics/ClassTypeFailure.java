package lfocc.features.classes.semantics;

import lfocc.framework.compiler.ast.ASTVisitor.VisitorFailure;

@SuppressWarnings("serial")
public class ClassTypeFailure extends VisitorFailure {

	public ClassTypeFailure(String message) {
		super(message);
	}
}