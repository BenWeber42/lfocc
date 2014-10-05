package lfocc.features.javali.semantics;

import lfocc.framework.compiler.ast.ASTVisitor.VisitorFailure;

@SuppressWarnings("serial")
public class EntryPointFailure extends VisitorFailure {

	public EntryPointFailure(String message) {
		super(message);
	}

}
