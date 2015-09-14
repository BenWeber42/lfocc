package lfocc.features.x86.backend.preparation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lfocc.features.base.ast.ScopeKind;
import lfocc.features.classes.ast.ClassDeclaration;
import lfocc.features.classes.ast.ClassType;
import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.globalscope.ast.GlobalScope;
import lfocc.features.types.semantics.TypeDB;
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
		
		if (node instanceof GlobalScope) {
			((GlobalScope) node).add(((ClassType) TypeDB.INSTANCE.getType("Object")).getNode());
			super.visit(node);
		} else if (node instanceof ClassDeclaration)
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
	
	public static class MethodOffset {
		public final int offset;
		
		private MethodOffset(int offset) {
			this.offset = offset;
		}
		
		public static int getOffset(FunctionDeclaration func) {
			assert func.extension(ScopeKind.class) == ScopeKind.CLASS_MEMBER;
			MethodOffset offset = func.extension(MethodOffset.class);
			assert offset != null;
			return offset.offset;
		}
		
		public static void setOffset(FunctionDeclaration func, int offset) {
			assert func.extension(ScopeKind.class) == ScopeKind.CLASS_MEMBER;
			func.extend(new MethodOffset(offset));
		}
	}
	
	public static class ClassTable {
		// keep space for link to parent
		private int offset = CodeGeneratorHelper.WORD_SIZE;
		private Map<String, Integer> offsets = new HashMap<String, Integer>();
		private List<FunctionDeclaration> jumpTable = new ArrayList<FunctionDeclaration>();
		
		public ClassTable(ClassDeclaration classDecl) {
			assert classDecl.getType().getParent() == null ||
					classDecl.getType().getParent().getNode().extension(ClassTable.class) != null;
			
			if (classDecl.getType().getParent() != null) {
				ClassTable parent = classDecl.getType().getParent().getNode().extension(ClassTable.class);
				offsets.putAll(parent.offsets);
				offset = parent.offset;
				jumpTable.addAll(parent.jumpTable);
			}
			
			for (ASTNode member: classDecl.getMembers()) {
				if (member instanceof FunctionDeclaration) {
					FunctionDeclaration func = (FunctionDeclaration) member;

					if (offsets.containsKey(func.getName())) {
						jumpTable.set(offsets.get(func.getName())/CodeGeneratorHelper.WORD_SIZE - 1, func);
						MethodOffset.setOffset(func, offsets.get(func.getName()));
					} else {
						offsets.put(func.getName(), offset);
						MethodOffset.setOffset(func, offset);
						offset += CodeGeneratorHelper.WORD_SIZE;
						jumpTable.add(func);
					}
				}
			}
			
			assert jumpTable.size()*CodeGeneratorHelper.WORD_SIZE + CodeGeneratorHelper.WORD_SIZE == offset;
		}
		
		public Iterable<FunctionDeclaration> getJumpTable() {
			return jumpTable;
		}
	}
	
	public static class InstanceTable {
		// keep space for link to ClassTable
		int offset = CodeGeneratorHelper.WORD_SIZE;
		Map<String, Integer> offsets = new HashMap<String, Integer>();
		
		public boolean has(String name) {
			return offsets.containsKey(name);
		}
		
		public int get(String name) {
			assert has(name);
			return offsets.get(name);
		}
		
		public void add(String name) {
			assert !has(name);

			offsets.put(name, offset);
			offset += CodeGeneratorHelper.WORD_SIZE;
		}
		
		public int getSize() {
			return offset;
		}
		
		/*
		 * Classes may not have variables as class members.
		 * Nevertheless, the InstanceTable is required for polymorphism,
		 * particularly when the dynamic type differs from the static type
		 * so that function calls get resolved correctly.
		 * 
		 * Therefore, the InstanceTable will always contain a link to the
		 * ClassTable. But only if variables can be classmembers, will it be
		 * filled with those offsets (see ClassVariablePreparer).
		 */
	}
}
