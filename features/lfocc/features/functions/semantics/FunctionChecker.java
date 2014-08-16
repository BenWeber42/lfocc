package lfocc.features.functions.semantics;

import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.classes.ast.ClassDeclaration;
import lfocc.features.classes.ast.ClassType;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTTransformer;

public class FunctionChecker extends ASTTransformer {
	
	private ClassType clazz = null;
	
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
		
		
		transform(func.getChildren());
	}
	
	private void classDeclaration(ClassDeclaration classDeclaration) throws TransformerFailure {
		ClassType prevClazz = clazz;
		clazz = classDeclaration.getType();
		
		transform(classDeclaration.getChildren());
		
		// TODO: check inheritance/shadowing/overloading
		
		clazz = prevClazz;
	}

}
