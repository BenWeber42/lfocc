package lfocc.features.javali.semantics;

import lfocc.framework.compiler.ast.ASTVisitor.VisitorFailure;

public class EntryPointFailure extends VisitorFailure {
	private static final long serialVersionUID = -3754911823268963891L;

	public EntryPointFailure(String message) {
		super(message);
	}

}
