package lfocc.features.variables.semantics;

import lfocc.features.types.ast.TypeSymbol;
import lfocc.features.types.semantics.TypeDB;
import lfocc.features.variables.ast.VariableDeclaration;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

/*
 * Checks types of variable declarations
 *    - type of variable declaration exists
 *    - resolves types of variable declarations
 *    - doesn't resolve types of variable use
 */
public class VariableTypeLookup extends ASTVisitor {

	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		
		if (node instanceof VariableDeclaration) {
			VariableDeclaration var = (VariableDeclaration) node;
			
			TypeSymbol type = TypeDB.INSTANCE.getType(var.getType().getName());
			
			if (type == null) {
				throw new VariableFailure(String.format("Unknown type '%s' for variable '%s'!",
					var.getType().getName(), var.getName()));
			}
			
			var.setType(type);
		} else {
			super.visit(node);
		}
	}
	
	public static class VariableFailure extends VisitorFailure {
		private static final long serialVersionUID = 6371314933293890502L;

		public VariableFailure(String message) {
			super(message);
		}
		
	}
}
