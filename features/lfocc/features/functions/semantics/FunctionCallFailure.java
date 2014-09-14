package lfocc.features.functions.semantics;

import lfocc.framework.compiler.ast.ASTVisitor.VisitorFailure;

@SuppressWarnings("serial")
public class FunctionCallFailure extends VisitorFailure {

	public FunctionCallFailure(String message) {
		super(message);
	}

}
