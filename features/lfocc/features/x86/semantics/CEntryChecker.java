package lfocc.features.x86.semantics;

import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.functions.ast.FunctionScope;
import lfocc.features.functions.ast.VoidType;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

public class CEntryChecker extends ASTVisitor {

	@Override
	public void visit(ASTNode node) throws EntryPointFailure {
		
		// TODO: testing
		
		if (node.extension(FunctionScope.class) == null) {
			throw new EntryPointFailure("No 'main' function defined!");
		}
		
		FunctionDeclaration main = node.extension(FunctionScope.class).getMethod("main");
		
		if (main == null) {
			throw new EntryPointFailure("No 'main' function defined!");
		}
		
		if (!(main.getReturnType() instanceof VoidType)) {
			throw new EntryPointFailure(String.format(
					"'main' method must return 'void' (instead of '%s')!",
					main.getReturnType().getName()));
		}
		
		if (main.getParameters().size() != 0) {
			throw new EntryPointFailure(String.format(
					"'main' method must have no parameters (instead of '%s')!",
					main.getParameters().size()));
		}
	}
}
