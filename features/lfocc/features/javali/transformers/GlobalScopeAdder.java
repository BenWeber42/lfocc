package lfocc.features.javali.transformers;

import java.util.Iterator;

import lfocc.features.classes.ast.ClassDeclaration;
import lfocc.features.classes.ast.ClassType;
import lfocc.features.globalscope.ast.GlobalScope;
import lfocc.features.types.ast.TypeSymbol;
import lfocc.features.types.semantics.TypeDB;
import lfocc.features.variables.ast.VariableDeclaration;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

/*
 * This transformer is dependent on variables, assignments, classes
 */
public class GlobalScopeAdder extends ASTVisitor {
	
	public static final String ESCAPE = "escape";
	public static final String GLOBAL_SCOPE = "GlobalScope";
	public static final String GLOBALS = "globals";

	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		
		if (node instanceof GlobalScope) {
			globalScope((GlobalScope) node);
		}
		
		visit(node.getChildren());
	}
	
	private void globalScope(GlobalScope globals) {
		// escape all class names
		Iterator<TypeSymbol> types = TypeDB.INSTANCE.iterator();
		while (types.hasNext()) {
			TypeSymbol type = types.next();
			if (type instanceof ClassType) {
				ClassType clazz = (ClassType) type;
				clazz.setName(escape(ESCAPE, GLOBAL_SCOPE, clazz.getName()));
			}
		}
		
		// add globalscope class
		ClassType globalScope = new ClassType(new ClassDeclaration(GLOBAL_SCOPE));
		globals.getChildren().add(new VariableDeclaration(globalScope, GLOBALS));
		TypeDB.INSTANCE.addType(globalScope);
		
		// add globals attribute of type GlobalScope to each class
		types = TypeDB.INSTANCE.iterator();
		while (types.hasNext()) {
			TypeSymbol type = types.next();
			if (type instanceof ClassType) {
				ClassType clazz = (ClassType) type;
				
				Iterator<ASTNode> members = clazz.getNode().getMembers().iterator();
				while (members.hasNext()) {
					ASTNode member = members.next();
					if (member instanceof VariableDeclaration) {
						VariableDeclaration var = (VariableDeclaration) member;
						var.setName(escape(ESCAPE, GLOBALS, var.getName()));
					}
				}
				
				clazz.getNode().getMembers().add(0,
						new VariableDeclaration(globalScope, GLOBALS)
						);
			}
		}
	}
	
	public static String escape(String escape, String symbol, String target) {
		
		String result = target.replace(escape, escape + escape);
		result = result.replace(symbol, escape + symbol);
		return result;
	}
}
