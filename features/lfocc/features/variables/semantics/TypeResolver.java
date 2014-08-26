package lfocc.features.variables.semantics;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import lfocc.features.classes.ast.ClassDeclaration;
import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.globalscope.ast.GlobalScope;
import lfocc.features.types.ast.TypeSymbol;
import lfocc.features.types.semantics.TypeDB;
import lfocc.features.variables.ast.VariableDeclaration;
import lfocc.features.variables.ast.VariableScope;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

public class TypeResolver extends ASTVisitor {
	
	private VariableScope root = new VariableScope(null);
	private Stack<VariableScope> stack = new Stack<VariableScope>();
	
	public TypeResolver() {
		stack.push(root);
	}
	
	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		
		if (node instanceof GlobalScope) {
			globalScope((GlobalScope) node);
		} else if (node instanceof ClassDeclaration) {
			classDeclaration((ClassDeclaration) node);
		} else if (node instanceof FunctionDeclaration) {
			node.extend(new VariableScope(stack.peek()));
			stack.push(node.extension(VariableScope.class));
			collectScope(node.getChildren());
			visit(node.getChildren());
			stack.pop();
		} else if (containsVariableDeclaration(node.getChildren())) {
			node.extend(new VariableScope(stack.peek()));
			stack.push(node.extension(VariableScope.class));
			collectScope(node.getChildren());
			visit(node.getChildren());
			stack.pop();
		} else {
			visit(node.getChildren());
		}
	}
	
	private boolean containsVariableDeclaration(List<ASTNode> nodes) {
		Iterator<ASTNode> it = nodes.iterator();
		while (it.hasNext()) {
			if (it.next() instanceof VariableDeclaration)
				return true;
		}
		return false;
	}
	
	private void globalScope(GlobalScope global) throws VisitorFailure {
		global.extend(root);
		collectScope(global.getChildren());
		visit(global.getChildren());
	}

	private void classDeclaration(ClassDeclaration clazz) throws VisitorFailure {
		
		if (clazz.extension(VariableScope.class) != null)
			return;
		
		if (clazz.getType().getParent() == null) {
			// clazz should be 'Object', so the next lower scope is the global scope
			clazz.extend(new VariableScope(root));
		} else {
			// get the lower scope from the parent class:
			if (clazz.getType().getParent().getNode().extension(VariableScope.class) == null) {
				classDeclaration(clazz.getType().getParent().getNode());
			}
			clazz.extend(new VariableScope(
					clazz.getType().getParent().getNode().extension(VariableScope.class)));
		}
		
		stack.push(clazz.extension(VariableScope.class));
		collectScope(clazz.getChildren());
		visit(clazz.getChildren());
		stack.pop();
	}
	
	private void collectScope(List<ASTNode> nodes) throws VariableFailure {
		Iterator<ASTNode> it = nodes.iterator();
		while (it.hasNext()) {
			ASTNode child = it.next();
			if (child instanceof VariableDeclaration) {
				variableDeclaration((VariableDeclaration) child);
			}
		}
	}

	private void variableDeclaration(VariableDeclaration var) throws VariableFailure {
		TypeSymbol type = TypeDB.INSTANCE.getType(var.getType().getName());
		
		if (type == null) {
			throw new VariableFailure(String.format("Unknown type '%s' for variable '%s'!",
					var.getType().getName(), var.getName()));
		}

		var.setType(type);
		
		if (stack.peek().getVariable(var.getName()) != null) {
			throw new VariableFailure(String.format("Variable '%s' already declared!",
					var.getName()));
		}
		
		stack.peek().addVariable(var);
	}
	
	@SuppressWarnings("serial")
	public static class VariableFailure extends VisitorFailure {

		public VariableFailure(String message) {
			super(message);
		}
		
	}
}
