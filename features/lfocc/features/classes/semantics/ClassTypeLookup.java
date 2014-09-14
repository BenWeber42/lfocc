package lfocc.features.classes.semantics;

import lfocc.features.classes.ast.CastExpression;
import lfocc.features.classes.ast.ClassDeclaration;
import lfocc.features.classes.ast.ClassType;
import lfocc.features.classes.ast.NewOperator;
import lfocc.features.classes.ast.ThisReference;
import lfocc.features.types.ast.TypeSymbol;
import lfocc.features.types.semantics.TypeDB;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

/*
 * Checks for
 * - correct use of this
 *    - this
 *    - resolves type of this
 * - correct use of new
 *    - Class of new exists
 *    - resolves type of new
 * - (partially) correct use of the cast operator
 *    - cast's argument exists and is a class
 *    - resolves type of cast
 *    - doesn't check for valid up or down cast (done in a different stage)
 */
public class ClassTypeLookup extends ASTVisitor {
	
	private ClassType currentClass = null;

	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		
		if (node instanceof ClassDeclaration) {
			currentClass = ((ClassDeclaration) node).getType();
			visit(node.getChildren());
			currentClass = null;
		} else if (node instanceof ThisReference) {
			if (currentClass == null) {
				throw new ClassTypeFailure(String.format("'this' used outside of a class!"));
			}

			((ThisReference) node).setType(currentClass);
		} else if (node instanceof NewOperator) {
			NewOperator newOp = (NewOperator) node;
			
			TypeSymbol type = TypeDB.INSTANCE.getType(newOp.getClassName());

			if (type == null) {
				throw new ClassTypeFailure(String.format("Unknown class '%s' in new operator!",
						newOp.getClassName()));
			} else if (!(type instanceof ClassType)) {
				throw new ClassTypeFailure(String.format(
						"'new' can only be used with class types, '%s' isn't a classtype!",
						newOp.getClassName()));
			}
			
			newOp.setType((ClassType) type);
		} else if (node instanceof CastExpression) {
			CastExpression cast = (CastExpression) node;
			
			TypeSymbol type = TypeDB.INSTANCE.getType(cast.getCast());
			
			if (type == null) {
				throw new ClassTypeFailure(String.format("Unknown class '%s' in cast operator!",
						cast.getCast()));
			} else if (!(type instanceof ClassType)) {
				throw new ClassTypeFailure(String.format(
						"'cast' can only be used with class types, '%s' isn't a classtype!",
						cast.getCast()));
			}
			
			cast.setType((ClassType) type);
			
			visit(cast.getChildren());
		} else {
			super.visit(node);
		}
		
	}
	
	@SuppressWarnings("serial")
	public static class ClassTypeFailure extends VisitorFailure {

		public ClassTypeFailure(String message) {
			super(message);
		}
	}
}
