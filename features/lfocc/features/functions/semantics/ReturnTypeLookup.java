package lfocc.features.functions.semantics;

import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.types.ast.TypeSymbol;
import lfocc.features.types.semantics.TypeDB;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

/*
 * Checks for correct return types in function declarations
 *    - return type exists
 *    - sets return type of function declaration
 *    - doesn't check for correct types of returned expressions
 */
public class ReturnTypeLookup extends ASTVisitor {

	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		
		if (node instanceof FunctionDeclaration) {
			FunctionDeclaration func = (FunctionDeclaration) node;
			TypeSymbol ret = TypeDB.INSTANCE.getType(func.getReturnType().getName());
			
			if (ret == null) {
				throw new ReturnLookupFailure(String.format(
						"Unknown return type '%s' in function '%s'!",
						func.getReturnType().getName(), func.getName()));
			}
			
			func.setReturnType(ret);
		} else {
			super.visit(node);
		}
	}
	
	@SuppressWarnings("serial")
	public static class ReturnLookupFailure extends VisitorFailure {

		public ReturnLookupFailure(String message) {
			super(message);
		}
		
	}
}
