package lfocc.features.functions.semantics;

import lfocc.framework.compiler.ast.ASTVisitor.VisitorFailure;

public class ReturnFailure extends VisitorFailure {
	private static final long serialVersionUID = -2663813445610423442L;

	public ReturnFailure(String message) {
		super(message);
	}

}
