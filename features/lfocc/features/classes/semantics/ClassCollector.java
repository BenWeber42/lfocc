package lfocc.features.classes.semantics;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lfocc.features.classes.ast.ClassDeclaration;
import lfocc.features.classes.ast.ClassType;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTTransformer;
import lfocc.features.types.ast.TypeSymbol;
import lfocc.features.types.semantics.TypeDB;

public class ClassCollector extends ASTTransformer { 
	
	@Override
	public void transform(List<ASTNode> nodes) throws TransformerFailure {
		ClassType obj = new ClassType(new ClassDeclaration("Object"));
		obj.setParent(null);
		TypeDB.INSTANCE.addType(obj);
		
		super.transform(nodes);
		
		inheritance();
	}

	@Override
	public void visit(ASTNode node) {
		if (node instanceof ClassDeclaration) {
			ClassType classType = new ClassType((ClassDeclaration) node);
			TypeDB.INSTANCE.addType(classType);
			((ClassDeclaration) node).setType(classType);
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
	
	@SuppressWarnings("serial")
	public static class InheritanceFailure extends TransformerFailure {

		public InheritanceFailure(String message) {
			super(message);
		}
		
	}

}
