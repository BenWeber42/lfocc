package lfocc.features.x86.backend.preparation;

import lfocc.features.functions.ast.FunctionScope;
import lfocc.features.globalscope.ast.GlobalScope;
import lfocc.features.x86.backend.CodeGeneratorHelper.NoNameEscape;
import lfocc.features.x86.backend.CodeGeneratorHelper.EntryPoint;
import lfocc.framework.compiler.ast.ExtendableNode;

public class CEntryAdder {
	
	public static void addCEntry(GlobalScope globalScope) {
		ExtendableNode node = globalScope.extension(FunctionScope.class).getMethod("main");
		node.extend(new NoNameEscape());
		node.extend(new EntryPoint());
	}
}
