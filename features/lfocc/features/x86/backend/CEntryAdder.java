package lfocc.features.x86.backend;

import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.globalscope.ast.GlobalScope;
import lfocc.features.x86.backend.CodeGeneratorInterface.NoNameEscape;
import lfocc.framework.compiler.ast.ASTNode;

public class CEntryAdder {
	
	public static void addCEntry(GlobalScope globalScope) {
		for (ASTNode child: globalScope.getChildren())
			if (child instanceof FunctionDeclaration
					&& ((FunctionDeclaration) child).getName().equals("main")) {
				child.extend(new NoNameEscape());
				break;
			}
		
		assert false;
	}

}
