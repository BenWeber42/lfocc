package lfocc.features.x86.backend;

import lfocc.features.globalscope.ast.GlobalScope;
import lfocc.framework.compiler.Backend.BackendFailure;
import lfocc.framework.compiler.ast.ASTNode;

public interface CodeGeneratorInterface {
	public String generate(GlobalScope root) throws BackendFailure;
	public String dispatch(ASTNode node) throws BackendFailure;
}
