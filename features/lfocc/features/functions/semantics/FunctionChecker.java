package lfocc.features.functions.semantics;

import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.functions.ast.MethodCollection;
import lfocc.features.types.ast.TypeSymbol;
import lfocc.features.types.semantics.TypeDB;
import lfocc.features.classes.ast.ClassDeclaration;
import lfocc.features.classes.ast.ClassType;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

public class FunctionChecker extends ASTVisitor {
	
	private ClassType clazz = null;
	private FunctionDeclaration func = null;
	
	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		if (node instanceof FunctionDeclaration)
			functionDeclaration((FunctionDeclaration) node);
		else if (node instanceof ClassDeclaration)
			classDeclaration((ClassDeclaration) node);
		else
			visit(node.getChildren());
	}
	
	private void functionDeclaration(FunctionDeclaration func) throws VisitorFailure {
		
		if (clazz != null)
			((MethodCollection) clazz.getNode().extension(MethodCollection.class)).addMethod(func);
		
		TypeSymbol ret = TypeDB.INSTANCE.getType(func.getReturnType().getName());

		if (ret == null)
			throw new FunctionFailure(String.format(
					"Unknown return type '%s' in function '%s'!",
					func.getReturnType().getName(), func.getName()));
			
		func.setReturnType(ret);
		
		// TODO: parameters
		
		FunctionDeclaration prevFunc = this.func;
		this.func = func;

		visit(func.getCode());
		
		this.func = prevFunc;
	}
	
	private void classDeclaration(ClassDeclaration classDeclaration) throws VisitorFailure {
		ClassType prevClazz = clazz;
		clazz = classDeclaration.getType();
		
		classDeclaration.attach(new MethodCollection());
		
		visit(classDeclaration.getChildren());
		
		// TODO: check inheritance/shadowing/overloading
		
		clazz = prevClazz;
	}

	@SuppressWarnings("serial")
	public static class FunctionFailure extends VisitorFailure {

		public FunctionFailure(String message) {
			super(message);
		}
	}
}