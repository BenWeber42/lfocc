package lfocc.features.x86.backend.preparation;

import java.util.HashSet;
import java.util.Set;

import lfocc.features.base.ast.ScopeKind;
import lfocc.features.classes.ast.ClassDeclaration;
import lfocc.features.variables.ast.VariableDeclaration;
import lfocc.features.x86.backend.preparation.ClassPreparer.InstanceTable;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

public class ClassVariablePreparer extends ASTVisitor {
	
	private Set<ClassDeclaration> done = new HashSet<ClassDeclaration>();

	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		if (node instanceof ClassDeclaration)
			classDeclaration((ClassDeclaration) node);
		else
			super.visit(node);
	}
	
	public static class AttributeOffset {
		public final int offset;
		
		private AttributeOffset(int offset) {
			this.offset = offset;
		}
		
		public static void setOffset(VariableDeclaration var, int offset) {
			assert var.extension(ScopeKind.class) == ScopeKind.CLASS_MEMBER;
			var.extend(new AttributeOffset(offset));
		}
		
		public static int getOffset(VariableDeclaration var) {
			assert var.extension(ScopeKind.class) == ScopeKind.CLASS_MEMBER;
			AttributeOffset offset = var.extension(AttributeOffset.class);
			assert offset != null;
			return offset.offset;
		}
	}
	
	private void classDeclaration(ClassDeclaration classDecl) {
		
		InstanceTable table = classDecl.extension(InstanceTable.class);

		assert table != null;
		
		if (done.contains(classDecl))
			return;
		
		done.add(classDecl);
		
		if (classDecl.getType().getParent() != null) {
			if (!done.contains(classDecl.getType().getParent().getNode()))
				classDeclaration(classDecl.getType().getParent().getNode());
			
			InstanceTable parent = classDecl.getType().getParent().getNode()
					.extension(InstanceTable.class);
			
			table.offset = parent.offset;
			table.offsets.putAll(parent.offsets);
		}
		
		for (ASTNode member: classDecl.getMembers()) {
			if (member instanceof VariableDeclaration) {
				VariableDeclaration var = (VariableDeclaration) member;
				String name = var.getName();

				if (!table.has(name))
					table.add(name);
				
				AttributeOffset.setOffset(var, table.get(name));
			}
		}
	}
}
