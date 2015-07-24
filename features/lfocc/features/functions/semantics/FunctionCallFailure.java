package lfocc.features.functions.semantics;

import lfocc.framework.compiler.ast.ASTVisitor.VisitorFailure;

public class FunctionCallFailure extends VisitorFailure {
	private static final long serialVersionUID = -925205191451527352L;

	public FunctionCallFailure(String message) {
		super(message);
	}

}
