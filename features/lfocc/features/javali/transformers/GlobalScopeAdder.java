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
		
		visit(node.getChildren());
		
		if (node instanceof GlobalScope) {
			globalScope((GlobalScope) node);
		} else if (node instanceof VariableDeclaration) {
			variableDeclaration((VariableDeclaration) node);
		}
	}
	
	private void variableDeclaration(VariableDeclaration var) {
		var.setName(escape(ESCAPE, GLOBALS, var.getName()));
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
		globals.add(new VariableDeclaration(globalScope, GLOBALS));
		globals.add(globalScope.getNode());
		TypeDB.INSTANCE.addType(globalScope);
		
		// add globals attribute of type GlobalScope to each class
		types = TypeDB.INSTANCE.iterator();
		while (types.hasNext()) {
			TypeSymbol type = types.next();
			if (type instanceof ClassType) {
				ClassType clazz = (ClassType) type;
				
				VariableDeclaration globalsAttribute = 
						new VariableDeclaration(globalScope, GLOBALS);
				
				clazz.getNode().getMembers().add(0, globalsAttribute);
				clazz.getNode().extend(globalsAttribute);
			}
		}
	}
	
	public static String escape(String escape, String symbol, String target) {
		
		String result = target.replace(escape, escape + escape);
		result = result.replace(symbol, escape + symbol);
		return result;
	}
}
