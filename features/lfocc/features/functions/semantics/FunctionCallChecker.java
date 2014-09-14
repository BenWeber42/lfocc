package lfocc.features.functions.semantics;

import lfocc.features.functions.ast.FunctionCall;
import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

/*
 * Checks for (independent of the Types feature):
 * - Correct function calls
 *    - amount of arguments given
 */
public class FunctionCallChecker extends ASTVisitor {
	
	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		
		if (node instanceof FunctionCall) {
			functionCall((FunctionCall) node);
		}
		
		visit(node.getChildren());
	}
	
	private void functionCall(FunctionCall func) throws FunctionCallFailure {
		
		FunctionDeclaration decl = func.getDeclaration();
		
		if (decl.getParameters().size() != func.getArguments().size()) {
			throw new FunctionCallFailure(String.format(
					"Function call '%s' has incorrect amount of arguments!",
					func.getName()
					));
		}
	}

}
