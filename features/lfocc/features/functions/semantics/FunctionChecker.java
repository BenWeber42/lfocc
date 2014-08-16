package lfocc.features.functions.semantics;

import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.functions.ast.FunctionSymbol;
import lfocc.features.functions.ast.MethodCollection;
import lfocc.features.functions.ast.ReturnStatement;
import lfocc.features.types.ast.TypeSymbol;
import lfocc.features.types.semantics.TypeDB;
import lfocc.features.classes.ast.ClassDeclaration;
import lfocc.features.classes.ast.ClassType;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTTransformer;

public class FunctionChecker extends ASTTransformer {
	
	private ClassType clazz = null;
	private FunctionSymbol func = null;
	
	@Override
	public void visit(ASTNode node) throws TransformerFailure {
		if (node instanceof FunctionDeclaration)
			functionDeclaration((FunctionDeclaration) node);
		else if (node instanceof ClassDeclaration)
			classDeclaration((ClassDeclaration) node);
		else
			transform(node.getChildren());
	}
	
	private void functionDeclaration(FunctionDeclaration func) throws TransformerFailure {
		
		FunctionSymbol sym = new FunctionSymbol(func);
		func.setSym(sym);
		
		if (clazz != null)
			((MethodCollection) clazz.getNode().extension(MethodCollection.class)).addMethod(sym);
		
		TypeSymbol ret = TypeDB.INSTANCE.getType(func.getReturnType().getName());

		if (ret == null)
			throw new FunctionFailure(String.format(
					"Unknown return type '%s' in function '%s'!",
					func.getReturnType().getName(), func.getName()));
			
		sym.setReturnType(ret);
		
		FunctionSymbol prevFunc = this.func;
		this.func = sym;

		transform(func.getChildren());
		
		this.func = prevFunc;
	}
	
	private void classDeclaration(ClassDeclaration classDeclaration) throws TransformerFailure {
		ClassType prevClazz = clazz;
		clazz = classDeclaration.getType();
		
		classDeclaration.attach(MethodCollection.class, new MethodCollection());
		
		transform(classDeclaration.getChildren());
		
		// TODO: check inheritance/shadowing/overloading
		
		clazz = prevClazz;
	}

	@SuppressWarnings("serial")
	public static class FunctionFailure extends TransformerFailure {

		public FunctionFailure(String message) {
			super(message);
		}
	}
}
