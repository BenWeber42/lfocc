package lfocc.features.x86.backend;

import lfocc.features.globalscope.ast.GlobalScope;

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

}
