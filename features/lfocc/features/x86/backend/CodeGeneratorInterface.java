package lfocc.features.x86.backend;

import lfocc.features.globalscope.ast.GlobalScope;

public interface CodeGeneratorInterface {
	public String generate(GlobalScope root);
}
