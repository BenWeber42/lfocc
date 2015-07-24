package lfocc.features.classes.semantics;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import lfocc.features.classes.ast.ClassDeclaration;
import lfocc.features.classes.ast.ClassType;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;
import lfocc.features.types.ast.TypeSymbol;
import lfocc.features.types.semantics.TypeDB;

/*
 * Collects all declared classes and checks for:
 * - Correct inheritance
 *    - Superclass exists
 *    - No circular inheritance
 *    - resolves parent class
 */
public class ClassCollector extends ASTVisitor { 
	
	private ClassType obj = null;
	
	public ClassCollector() {
		obj = new ClassType(new ClassDeclaration("Object"));
		obj.setParent(null);
		obj.getNode().setType(obj);
		TypeDB.INSTANCE.addType(obj);
	}

	@Override
	public void finish() throws InheritanceFailure {
		inheritance();
		// this will have been overwritten by inheritance()
		obj.setParent(null);
	}

	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		if (node instanceof ClassDeclaration) {
			ClassType classType = new ClassType((ClassDeclaration) node);

			if (TypeDB.INSTANCE.getType(classType.getName()) != null) {
				throw new InheritanceFailure(String.format(
						"Class '%s' already exists!", classType.getName()));
			}

			TypeDB.INSTANCE.addType(classType);
			((ClassDeclaration) node).setType(classType);

		} else {
			super.visit(node);
		}
	}
	
	private void inheritance() throws InheritanceFailure {
		
		Iterator<TypeSymbol> it = TypeDB.INSTANCE.iterator();
		while (it.hasNext()) {
			TypeSymbol type = it.next();
			if (type instanceof ClassType) {
				follow((ClassType) type);
			}
		}
	}
	
	private void follow(ClassType clazz) throws InheritanceFailure {
		Set<ClassType> supers = new HashSet<ClassType>();
		
		while (clazz.getNode().getParent() != null && !clazz.getNode().getParent().equals("Object")) {
			supers.add(clazz);

			if (clazz.getParent() == null) {
				TypeSymbol type = TypeDB.INSTANCE.getType(clazz.getNode().getParent());
				
				if (type != null && type instanceof ClassType) {
					ClassType parent = (ClassType) type;
					clazz.setParent(parent);
				} else {
					throw new InheritanceFailure(String.format(
							"Unknown superclass '%s' in class '%s'!",
							clazz.getNode().getParent(), clazz.getName()));
				}
			}

			if (supers.contains(clazz.getParent())) {
				throw new InheritanceFailure(String.format("Circular Inheritance with '%s' '%s'!",
						clazz.getName(), clazz.getParent().getName()));
			}
			
			clazz = clazz.getParent();
		}
		
		clazz.setParent((ClassType) TypeDB.INSTANCE.getType("Object"));
	}
	
	public static class InheritanceFailure extends VisitorFailure {
		private static final long serialVersionUID = 815859873435034530L;

		public InheritanceFailure(String message) {
			super(message);
		}
		
	}

}
