package lfocc.features.x86.semantics;

import lfocc.features.classes.ast.ClassType;
import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.functions.ast.FunctionScope;
import lfocc.features.functions.ast.VoidType;
import lfocc.features.types.ast.TypeSymbol;
import lfocc.features.types.semantics.TypeDB;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

public class JavaEntryChecker extends ASTVisitor {
	
	@Override
	public void visit(ASTNode node) throws EntryPointFailure {
		checkEntry();
	}
	
	private void checkEntry() throws EntryPointFailure {
		
		// TODO: testing
		
		TypeSymbol main = TypeDB.INSTANCE.getType("Main");
		
		if (main == null) {
			throw new EntryPointFailure(
					"No 'Main' class defined!"
					);
		}
		
		if (!(main instanceof ClassType)) {
			throw new EntryPointFailure(String.format(
					"'Main' type is not of class type (instead of '%s' type)!",
					main.getName()));
		}
		
		ClassType mainClass = (ClassType) main;
		
		if (mainClass.getNode().extension(FunctionScope.class) == null) {
			throw new EntryPointFailure("'Main' class has no 'main' method!");
		}
		
		FunctionDeclaration mainMethod = mainClass.getNode().extension(FunctionScope.class).getMethod("main");
		
		if (mainMethod == null) {
			throw new EntryPointFailure("'Main' class has no 'main' method!");
		}
		
		if (!(mainMethod.getReturnType() instanceof VoidType)) {
			throw new EntryPointFailure(String.format(
					"'main' method must return 'void' (instead of '%s')!",
					mainMethod.getReturnType().getName()));
		}
		
		if (mainMethod.getParameters().size() != 0) {
			throw new EntryPointFailure(String.format(
					"'main' method must have no parameters (instead of '%s')!",
					mainMethod.getParameters().size()));
		}
	}
}
