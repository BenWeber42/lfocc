package lfocc.features.x86.backend;

import java.util.List;

import lfocc.features.globalscope.ast.GlobalScope;
import lfocc.framework.compiler.ast.ASTNode;

/**
 * Dummy class so that java can compile the x86 feature
 * 
 * 
 * For every language a new CodeGenerator class will be generated tailored
 * to its needs.
 */
public class CodeGenerator implements CodeGeneratorInterface {

	@Override
	public String generate(GlobalScope root) {
		return null;
	}
	
	@Override
	public String dispatch(ASTNode node) {
		return null;
	}

	@Override
	public String dispatch(List<? extends ASTNode> node) {
		return null;
	}
	
	@Override
	public RegisterManager getRegisterManager() {
		return null;
	}

}
