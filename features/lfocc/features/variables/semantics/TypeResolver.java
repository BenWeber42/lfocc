package lfocc.features.variables.semantics;

import lfocc.features.types.ast.TypeSymbol;
import lfocc.features.types.semantics.TypeDB;
import lfocc.features.variables.ast.VariableDeclaration;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

public class TypeResolver extends ASTVisitor {
	
	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		
		if (!(node instanceof VariableDeclaration)) {
			visit(node.getChildren());
			return;
		}
			
		VariableDeclaration var = (VariableDeclaration) node;
		
		TypeSymbol type = TypeDB.INSTANCE.getType(var.getType().getName());
		
		if (type == null) {
			throw new VariableTypeFailure(String.format("Unknown type '%s' for variable '%s'!",
					var.getType().getName(), var.getName()));
		}

		var.setType(type);
	}
	
	@SuppressWarnings("serial")
	public static class VariableTypeFailure extends VisitorFailure {

		public VariableTypeFailure(String message) {
			super(message);
		}
		
	}
}
