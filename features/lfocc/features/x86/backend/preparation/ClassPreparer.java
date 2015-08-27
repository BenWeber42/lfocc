package lfocc.features.x86.backend.preparation;


import java.util.HashMap;
import java.util.Map;

import lfocc.features.classes.ast.ClassDeclaration;
import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;
import lfocc.features.x86.backend.CodeGeneratorHelper;
import lfocc.features.x86.backend.CodeGeneratorHelper.NameSpace;

public class ClassPreparer extends ASTVisitor {
	
	/*
	 * - Prepare class tables
	 * - Prepare instance tables
	 * - Mark class members with namespaces
	 */

	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		
		if (node instanceof ClassDeclaration)
			classDeclaration((ClassDeclaration) node);
		else
			super.visit(node);
		
	}
	
	private void classDeclaration(ClassDeclaration classDecl) {
		
		if (classDecl.extension(ClassTable.class) != null)
			return;
		
		if (classDecl.getType().getParent() != null &&
				classDecl.getType().getParent().getNode().extension(ClassTable.class) == null)

			classDeclaration(classDecl.getType().getParent().getNode());
		
		
		classDecl.extend(new ClassTable(classDecl));
		classDecl.extend(new InstanceTable());
		
		for (ASTNode node: classDecl.getMembers())
			node.extend(new NameSpace(classDecl.getName() + "__"));
	}
	
	public static class ClassTable {
		// keep space for link to parent
		private int offset = CodeGeneratorHelper.WORD_SIZE;
		private Map<String, Integer> offsets = new HashMap<String, Integer>();
		
		public ClassTable(ClassDeclaration classDecl) {
			assert classDecl.getType().getParent() == null ||
					classDecl.getType().getParent().getNode().extension(ClassTable.class) != null;
			
			if (classDecl.getType().getParent() != null) {
				ClassTable parent = classDecl.getType().getParent().getNode().extension(ClassTable.class);
				offsets.putAll(parent.offsets);
				offset = parent.offset;
			}
			
			for (ASTNode member: classDecl.getMembers()) {
				if (member instanceof FunctionDeclaration &&
						!offsets.containsKey(((FunctionDeclaration) member).getName())) {

					offsets.put(((FunctionDeclaration) member).getName(), offset);
					offset += CodeGeneratorHelper.WORD_SIZE;
				}
			}
			
		}
	}
	
	public static class InstanceTable {
		// keep space for link to ClassTable
		int offset = CodeGeneratorHelper.WORD_SIZE;
		Map<String, Integer> offsets = new HashMap<String, Integer>();
		
		public void add(String name) {
			if (offsets.containsKey(name))
				return;
			
			offsets.put(name, offset);
			offset += CodeGeneratorHelper.WORD_SIZE;
		}
		
		/*
		 * Classes may not have variables as class members.
		 * Nevertheless, the InstanceTable is required for polymorphism,
		 * particularly when the dynamic type differs from the static type
		 * so that function calls get resolved correctly.
		 * 
		 * Therefor, the InstanceTable will always contain a link to the
		 * ClassTable. But only if variables can be classmembers, will it be
		 * filled with those offsets (see ClassVariablePreparer).
		 */
	}
}
