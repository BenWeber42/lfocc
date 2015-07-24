package lfocc.features.javali.transformers;

import java.util.List;
import java.util.ListIterator;

import lfocc.features.classes.ast.ClassDeclaration;
import lfocc.features.classes.ast.ClassType;
import lfocc.features.expressions.ast.Expression;
import lfocc.features.functions.ast.FunctionCall;
import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.functions.ast.MethodCall;
import lfocc.features.globalscope.ast.GlobalScope;
import lfocc.features.types.semantics.TypeDB;
import lfocc.features.variables.ast.Attribute;
import lfocc.features.variables.ast.Variable;
import lfocc.features.variables.ast.VariableDeclaration;
import lfocc.framework.compiler.ast.ASTVisitor;
import lfocc.framework.compiler.ast.ASTNode;

public class GlobalsMover extends ASTVisitor {
	
	private VariableDeclaration globalsAttribute = null;

	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		
		if (node instanceof GlobalScope) {
			globalScope((GlobalScope) node);
		} else if (node instanceof ClassDeclaration) {
			assert globalsAttribute == null;
			assert node.extension(VariableDeclaration.class) != null;
			globalsAttribute = node.extension(VariableDeclaration.class);
		}
		
		visit(node.getChildren());
		
		if (node instanceof ClassDeclaration) {
			globalsAttribute = null;
		}
	}
	
	@Override
	public void visit(List<ASTSlot<? extends ASTNode>> nodes) throws VisitorFailure {
		
		// FIXME: getChildren sometimes creates new lists
		// so replacing elements in the list won't modify the nodes!
		ListIterator<ASTNode> it = nodes.listIterator();
		while (it.hasNext()) {
			ASTNode node = it.next();
			
			if (node instanceof Variable) {
				variable((Variable) node, it);
			} else if (node instanceof FunctionCall) {
				functionCall((FunctionCall) node, it);
			}
		}
	}
	
	private void globalScope(GlobalScope globals) {
		
		ClassType globalScope = (ClassType)
				TypeDB.INSTANCE.getType(GlobalScopeAdder.GLOBAL_SCOPE);
		
		ListIterator<ASTNode> children = globals.getChildren().listIterator();
		
		while (children.hasNext()) {
			ASTNode child = children.next();
			
			if (child instanceof FunctionDeclaration ||
					child instanceof VariableDeclaration) {
				
				children.remove();
				globalScope.getNode().getMembers().add(child);
				child.extend(new GlobalsMarker());
			}
		}
	}
	
	private void variable(Variable var, ListIterator<ASTNode> nodes) {
		if (var.getDeclaration().extension(GlobalsMarker.class) == null)
			return;
		
		// it's a global variable, need to replace it with an attribute
		
		Attribute attribute = new Attribute(new Variable(globalsAttribute), var.getName());
		nodes.set(attribute);
	}
	
	private void functionCall(FunctionCall func, ListIterator<ASTNode> nodes) {
		if (func.getDeclaration().extension(GlobalsMarker.class) == null)
			return;
		
		MethodCall method = new MethodCall(
				func.getName(),
				new Variable(globalsAttribute),
				func.getArguments());
		
		method.setDeclaration(func.getDeclaration());
		nodes.set(method);
	}
	
	
	private static class GlobalsMarker {}
}
