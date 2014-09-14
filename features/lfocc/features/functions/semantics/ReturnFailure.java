package lfocc.features.functions.semantics;

import lfocc.framework.compiler.ast.ASTVisitor.VisitorFailure;

@SuppressWarnings("serial")
public class ReturnFailure extends VisitorFailure {

	public ReturnFailure(String message) {
		super(message);
	}

}
