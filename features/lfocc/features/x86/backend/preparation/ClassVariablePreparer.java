package lfocc.features.x86.backend.preparation;

import java.util.HashSet;
import java.util.Set;

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
				table.add(((VariableDeclaration) member).getName());
			}
		}
	}
}
