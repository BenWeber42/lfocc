package lfocc.framework.compiler;

import lfocc.framework.compiler.ast.ASTNode;


public interface Backend {
	// TODO: excpetion in case of failure
	public void generate(StringBuilder output, ASTNode root);

}
