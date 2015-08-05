package lfocc.features.x86.backend;

import lfocc.features.functions.ast.FunctionScope;
import lfocc.features.globalscope.ast.GlobalScope;
import lfocc.features.x86.backend.CodeGeneratorInterface.NoNameEscape;

public class CEntryAdder {
	
	public static void addCEntry(GlobalScope globalScope) {
		globalScope.extension(FunctionScope.class).getMethod("main").extend(new NoNameEscape());
	}
}
