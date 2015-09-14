package lfocc.features.x86.backend;

import java.util.List;

import lfocc.features.expressions.ast.Expression;
import lfocc.features.globalscope.ast.GlobalScope;
import lfocc.framework.compiler.Backend.BackendFailure;
import lfocc.framework.compiler.ast.ASTNode;

public interface CodeGeneratorInterface {
	public String generate(GlobalScope root) throws BackendFailure;
	public String dispatch(ASTNode node) throws BackendFailure;
	public String dispatch(List<? extends ASTNode> nodes) throws BackendFailure;
	public String getAddress(Expression node) throws BackendFailure;
	public RegisterManager getRegisterManager();
	public LabelManager getLabelManager();
}
