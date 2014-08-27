package lfocc.features.functions.semantics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lfocc.features.classes.ast.ClassDeclaration;
import lfocc.features.expressions.ast.FloatType;
import lfocc.features.expressions.ast.IntType;
import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.functions.ast.FunctionScope;
import lfocc.features.functions.ast.VoidType;
import lfocc.features.globalscope.ast.GlobalScope;
import lfocc.features.types.ast.TypeSymbol;
import lfocc.features.types.semantics.TypeDB;
import lfocc.features.variables.ast.VariableDeclaration;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

public class FunctionChecker extends ASTVisitor {
	
	private FunctionScope root = new FunctionScope(null);
	
	public FunctionChecker() {
		
		List<VariableDeclaration> param = new ArrayList<VariableDeclaration>();

		param.add(new VariableDeclaration(new IntType(), "_int"));
		root.addMethod(new FunctionDeclaration(
				new VoidType(),
				"write",
				param,
				new ArrayList<ASTNode>()
				));

		param.clear();
		param.add(new VariableDeclaration(new FloatType(), "_float"));
		root.addMethod(new FunctionDeclaration(
				new VoidType(),
				"writef",
				param,
				new ArrayList<ASTNode>()
				));

		param.clear();
		root.addMethod(new FunctionDeclaration(
				new VoidType(),
				"writeln",
				param,
				new ArrayList<ASTNode>()
				));

		root.addMethod(new FunctionDeclaration(
				new IntType(),
				"read",
				param,
				new ArrayList<ASTNode>()
				));

		root.addMethod(new FunctionDeclaration(
				new FloatType(),
				"readf",
				param,
				new ArrayList<ASTNode>()
				));
	}
	
	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		
		if (node instanceof GlobalScope) {
			globalScope((GlobalScope) node);
		} else if (node instanceof ClassDeclaration) {
			classDeclaration((ClassDeclaration) node);
		}
		
	}
	
	private void globalScope(GlobalScope global) throws VisitorFailure {
		global.extend(root);
		
		Iterator<ASTNode> it = global.getChildren().iterator();
		while (it.hasNext()) {
			ASTNode child = it.next();
			if (child instanceof FunctionDeclaration) 
				functionDeclaration(root, null, (FunctionDeclaration) child);
		}
		
		visit(global.getChildren());
	}
	
	private void classDeclaration(ClassDeclaration clazz) throws FunctionFailure {
		
		if (clazz.extension(FunctionScope.class) != null)
			return;
		
		if (clazz.getType().getParent() == null) {
			// clazz should be 'Object', so the next lower scope is the global scope
			clazz.extend(new FunctionScope(root));
		} else {
			// get the lower scope from the parent class:
			if (clazz.getType().getParent().getNode().extension(FunctionScope.class) == null) {
				classDeclaration(clazz.getType().getParent().getNode());
			}
			clazz.extend(new FunctionScope(
					clazz.getType().getParent().getNode().extension(FunctionScope.class)));
		}
		
		Iterator<ASTNode> it = clazz.getChildren().iterator();
		while (it.hasNext()) {
			ASTNode child = it.next();
			if (child instanceof FunctionDeclaration)
				functionDeclaration(clazz.extension(FunctionScope.class),
						clazz, (FunctionDeclaration) child);
		}

	}
	
	private void functionDeclaration(FunctionScope scope, ClassDeclaration clazz, 
			FunctionDeclaration func) throws FunctionFailure {

		scope.addMethod(func);
		checkReturn(func);
		checkInheritance(scope, clazz, func);
	}
	
	private void checkReturn(FunctionDeclaration func) throws FunctionFailure {
		TypeSymbol ret = TypeDB.INSTANCE.getType(func.getReturnType().getName());
		if (ret == null)
			throw new FunctionFailure(String.format(
					"Unknown return type '%s' in function '%s'!",
					func.getReturnType().getName(), func.getName()));
		
		func.setReturnType(ret);
	}
	
	private void checkInheritance(FunctionScope scope, ClassDeclaration clazz,
			FunctionDeclaration func) throws FunctionFailure {

		if (scope.getParent() == null)
			return;
		
		FunctionDeclaration parent = scope.getParent().getMethod(func.getName());
		if (parent == null)
			return;
		
		if (!parent.getReturnType().equals(func.getReturnType())) {
			throw new FunctionFailure(String.format(
					"Function '%s' in class '%s' doesn't override correctly (return type mismatch)!",
					func.getName(), clazz.getName()));
		}
		
		if (parent.getParameters().size() != func.getParameters().size()) {
			throw new FunctionFailure(String.format(
					"Function '%s' in class '%s' doesn't override correctly! (number of parameters mismatch)",
					func.getName(), clazz.getName()));
		}
		
		Iterator<VariableDeclaration> parentParam = parent.getParameters().iterator();
		Iterator<VariableDeclaration> param = func.getParameters().iterator();
		
		while (parentParam.hasNext()) {
			assert param.hasNext();
			
			if (!parentParam.next().getType().equals(param.next().getType())) {
				throw new FunctionFailure(String.format(
						"Function '%s' in class '%s' doesn't override correctly! (parameter type mismatch)",
						func.getName(), clazz.getName()));
			}
		}

		assert !param.hasNext();
	}

	@SuppressWarnings("serial")
	public static class FunctionFailure extends VisitorFailure {

		public FunctionFailure(String message) {
			super(message);
		}
	}
}
