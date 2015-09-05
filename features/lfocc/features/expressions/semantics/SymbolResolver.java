package lfocc.features.expressions.semantics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import lfocc.features.base.ast.ScopeKind;
import lfocc.features.classes.ast.ClassDeclaration;
import lfocc.features.classes.ast.ClassType;
import lfocc.features.expressions.ast.FloatType;
import lfocc.features.expressions.ast.IntType;
import lfocc.features.functions.ast.FunctionCall;
import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.functions.ast.FunctionScope;
import lfocc.features.functions.ast.MethodCall;
import lfocc.features.functions.ast.VoidType;
import lfocc.features.globalscope.ast.GlobalScope;
import lfocc.features.variables.ast.Attribute;
import lfocc.features.variables.ast.Variable;
import lfocc.features.variables.ast.VariableDeclaration;
import lfocc.features.variables.ast.VariableScope;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

/*
 * There are strong dependencies amongst many features when it comes to
 * symbol resolution. The following examples show those:
 * 
 * // here the attribute can't be looked up before the return type is known
 * variable.method().attribute
 * 
 * // here the method can't be be looked up before the attribute's type is known
 * variable.attribute.method()
 * 
 * //Assuming a language supports operator overloading:
 * // Then the method can't be looked up until the operator's types are resolved
 * (A + B).method()
 * 
 * Due to these strong dependencies it's hard to achieve flexibility and simplicity
 * at the same time. High flexibility would lead to high complexity (conditional
 * code generation and code composition from many features).
 * 
 * However in our case there is luckily a not too complex solution that achieves
 * pretty good flexibility.
 * 
 * This is achieved through smart separation of the semantic checks, such that
 * different semantic checks can be composed flexibly.
 * 
 * Because operator overloading isn't supported the only expressions that can
 * have attributes or methods are:
 * - new operator
 * - this keyword
 * - cast operator
 * - variable use
 * - function call
 * 
 * The first three can easily be resolved (this is done in the ClassTypeLookup).
 * Function calls and variable uses are looked up inside the symbol resolver.
 * 
 * So then all requirements to lookup attributes and methods are met.
 * 
 * This visitor works no matter whether the Types feature is activated or not.
 * 
 * This visitor checks for:
 * - correct use of variables
 *    - variable declaration doesn't shadow or override variables in outer scope
 *      (this is an arbitrary decision regarding semantics that simplifies the
 *      semantic checks)
 *    - variable exists inside of scope when used
 *    - resolves variable declaration of variable use
 *    - resolves scopes of variables
 * - correct use of function calls
 *    - adds write, writef, writeln, read & readf functions to global scope
 *    - only functions are called that exist
 *    - methods don't shadow or override global functions
 *      (this is an arbitrary decision regarding semantics that simplifies the
 *      semantic checks)
 *    - methods override correctly
 *    - resolves function declarations of function calls
 * - correct use of attributes
 *    - attribute exists
 *    - resolves variable declaration of attribute
 * - correct use of method calls
 *    - method exists
 *    - resolves method declaration of method call
 */
public class SymbolResolver extends ASTVisitor {

	private Stack<VariableScope> variables = new Stack<VariableScope>();
	private Stack<FunctionScope> functions = new Stack<FunctionScope>();
	private ClassType currentClass = null;
	private boolean insideFunction = false;
	
	public SymbolResolver() {
		variables.push(new VariableScope(null));
		functions.push(new FunctionScope(null));

		FunctionDeclaration func;
		// add global write function
		func = new FunctionDeclaration(
				new VoidType(),
				"write",
				new ArrayList<VariableDeclaration>(Arrays.asList(new VariableDeclaration(new IntType(), "i"))),
				new ArrayList<ASTNode>()
				);
		func.extend(ScopeKind.GLOBAL);
		functions.peek().addMethod(func);

		// add global writef function
		func = new FunctionDeclaration(
				new VoidType(),
				"writef",
				new ArrayList<VariableDeclaration>(Arrays.asList(new VariableDeclaration(new FloatType(), "f"))),
				new ArrayList<ASTNode>()
				);
		func.extend(ScopeKind.GLOBAL);
		functions.peek().addMethod(func);

		// add global writeln function
		func = new FunctionDeclaration(
				new VoidType(),
				"writeln",
				new ArrayList<VariableDeclaration>(),
				new ArrayList<ASTNode>()
				);
		func.extend(ScopeKind.GLOBAL);
		functions.peek().addMethod(func);

		// add global read function
		func = new FunctionDeclaration(
				new IntType(),
				"read",
				new ArrayList<VariableDeclaration>(),
				new ArrayList<ASTNode>()
				);
		func.extend(ScopeKind.GLOBAL);
		functions.peek().addMethod(func);

		// add global readf function
		func = new FunctionDeclaration(
				new FloatType(),
				"readf",
				new ArrayList<VariableDeclaration>(),
				new ArrayList<ASTNode>()
				);
		func.extend(ScopeKind.GLOBAL);
		functions.peek().addMethod(func);
	}

	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		
		if (node instanceof GlobalScope) {
			globalScope((GlobalScope) node);
			return;
		} else if (node instanceof MethodCall) {
			methodCall((MethodCall) node);
		} else if (node instanceof FunctionCall) {
			functionCall((FunctionCall) node);
		} else if (node instanceof FunctionDeclaration) {
			insideFunction = true;
		} else if (insideFunction && node instanceof VariableDeclaration) {
			variableDeclaration((VariableDeclaration) node);
		} else if (node instanceof Variable) {
			variable((Variable) node);
		} else if (node instanceof Attribute) {
			attribute((Attribute) node);
		}
		
		if (node.extension(VariableScope.class) != null) { 
			variables.push(node.extension(VariableScope.class));
		} else {
			variables.push(new VariableScope(variables.peek()));
		}
		
		if (node.extension(FunctionScope.class) != null) {
			functions.push(node.extension(FunctionScope.class));
		}
		
		visit(node.getChildren());
		
		if (node.extension(FunctionScope.class) != null) {
			functions.pop();
		}
		
		if (!variables.peek().empty() && node.extension(VariableScope.class) == null) {
			node.extend(variables.peek());
		}
		variables.pop();
		
		if (node instanceof FunctionDeclaration) {
			insideFunction = false;
		}
			
	}

	private void classDeclaration(ClassDeclaration clazz) throws VisitorFailure {
		
		if (clazz.extension(VariableScope.class) != null
				|| clazz.extension(FunctionScope.class) != null) {
			
			assert clazz.extension(VariableScope.class) != null;
			assert clazz.extension(FunctionScope.class) != null;
			return;
		}
		
		if (clazz.getType().getParent() == null) {
			// Only 'Object's Parent is null
			assert clazz.getName().equals("Object");
			// 'Object's parent scope is the global scope
			assert functions.size() == 1 && variables.size() == 1;
			
			clazz.extend(new FunctionScope(functions.peek()));
			clazz.extend(new VariableScope(variables.peek()));
			
		} else {
			classDeclaration(clazz.getType().getParent().getNode());
			clazz.extend(new FunctionScope(clazz.getType().getParent().getNode().extension(FunctionScope.class)));
			clazz.extend(new VariableScope(clazz.getType().getParent().getNode().extension(VariableScope.class)));
		}
		
		variables.push(clazz.extension(VariableScope.class));
		functions.push(clazz.extension(FunctionScope.class));
		currentClass = clazz.getType();
		collectScope(clazz.getChildren());
		currentClass = null;
		variables.pop();
		functions.pop();
	}

	private void globalScope(GlobalScope root) throws VisitorFailure {
		assert functions.size() == 1 && variables.size() == 1;
		
		root.extend(functions.peek());
		root.extend(variables.peek());
		
		collectScope(root.getChildren());
		visit(root.getChildren());
	}

	private void attribute(Attribute attr) throws VisitorFailure {
		visit(attr.getExpr());
		
		if (!(attr.getExpr().getType() instanceof ClassType)) {
			throw new SymbolTypeFailure(String.format("Attribute '%s' requires a ClassType!",
					attr.getField()));
		}
		
		ClassDeclaration clazz = ((ClassType) attr.getExpr().getType()).getNode();
		
		assert clazz.extension(VariableScope.class) != null;
		
		if (clazz.extension(VariableScope.class).getVariable(attr.getField()) == null) {
			throw new SymbolFailure(String.format("Class '%s' has no attribute '%s'!",
					clazz.getName(), attr.getField()));
		}
		
		attr.setDeclaration(clazz.extension(VariableScope.class).getVariable(attr.getField()));
	}

	private void variable(Variable var) throws SymbolFailure {
		assert variables.peek() != null;
		
		VariableDeclaration decl = variables.peek().getVariable(var.getName());
		if (decl == null) {
			throw new SymbolFailure(String.format("Unknown variable '%s'!",
					var.getName()));
		}
		
		var.setDeclaration(decl);
	}

	private void methodCall(MethodCall method) throws VisitorFailure {
		visit(method.getExpr());
		
		if (!(method.getExpr().getType() instanceof ClassType)) {
			throw new SymbolTypeFailure(String.format("Method '%s' requires a ClassType!",
					method.getName()));
		}
		
		ClassDeclaration clazz = ((ClassType) method.getExpr().getType()).getNode();
		
		assert clazz.extension(FunctionScope.class) != null;
		
		if (clazz.extension(FunctionScope.class).getMethod(method.getName()) == null) {
			throw new SymbolFailure(String.format("Class '%s' has no method '%s'!",
					clazz.getName(), method.getName()));
		}
		
		method.setDeclaration(clazz.extension(FunctionScope.class).getMethod(method.getName()));
	}

	private void functionCall(FunctionCall func) throws SymbolFailure {
		assert functions.peek() != null;

		FunctionDeclaration decl = functions.peek().getMethod(func.getName());
		if (decl == null) {
			throw new SymbolFailure(String.format("Unknown function '%s'!",
					func.getName()));
		}
		
		func.setDeclaration(decl);
	}
	
	private void variableDeclaration(VariableDeclaration var) throws SymbolFailure {
		if (variables.peek().getVariable(var.getName()) != null) {
			throw new SymbolFailure(String.format("Variable '%s' already declared!",
					var.getName()));
		}
		
		variables.peek().addVariable(var);
		
		if (insideFunction)
			var.extend(ScopeKind.LOCAL);
		else if (currentClass != null)
			var.extend(ScopeKind.CLASS_MEMBER);
		else
			var.extend(ScopeKind.GLOBAL);
	}

	private void functionDeclaration(FunctionDeclaration func) throws VisitorFailure {
		if (functions.peek().getMethod(func.getName()) == null) {
			// No double declaration & no overriding -> everything good
			functions.peek().addMethod(func);
		} else if (functions.peek().getLocalMethod(func.getName()) != null) {
			// Double declaration -> error
			throw new SymbolFailure(String.format("Method '%s' already declared!",
					func.getName()));
		} else if (overrides(func, currentClass.getParent())) {
			// check correct overriding
			checkFunctionInheritance(func);
		} else {
			// no overriding -> double declaration -> error
			// this means a method overrides a global function
			throw new SymbolFailure(String.format("Invalid shadowing through method '%s'!",
					func.getName()));
		}
		
		func.extend(new VariableScope(variables.peek()));
		
		if (currentClass != null)
			func.extend(ScopeKind.CLASS_MEMBER);
		else
			func.extend(ScopeKind.GLOBAL);
	}
	
	private void checkFunctionInheritance(FunctionDeclaration func) throws SymbolFailure {
		FunctionDeclaration parent = functions.peek().getMethod(func.getName());

		// return type
		if (!parent.getReturnType().equals(func.getReturnType())) {
			throw new SymbolFailure(String.format(
					"Function '%s' in class '%s' doesn't override correctly (return type mismatch)!",
					func.getName(), currentClass.getName()));
		}
		
		// parameter count
		if (parent.getParameters().size() != func.getParameters().size()) {
			throw new SymbolFailure(String.format(
					"Function '%s' in class '%s' doesn't override correctly! (number of parameters mismatch)",
					func.getName(), currentClass.getName()));
		}
		
		// parameter types
		Iterator<VariableDeclaration> param = parent.getParameters().iterator();
		Iterator<VariableDeclaration> parentParam = func.getParameters().iterator();
		
		while (param.hasNext()) {
			if (!param.next().getType().equals(parentParam.next().getType())) {
				throw new SymbolFailure(String.format(
						"Function '%s' in class '%s' doesn't override correctly! (parameter type mismatch)",
						func.getName(), currentClass.getName()));
			}
		}
	}
	
	private boolean overrides(FunctionDeclaration func, ClassType clazz) {
		if (clazz.getNode().extension(FunctionScope.class).getLocalMethod(func.getName()) != null) {
			return true;
		} else if (clazz.getParent() != null) {
			return overrides(func, clazz.getParent());
		} else {
			return false;
		}
	}
	
	private void collectScope(List<ASTNode> nodes) throws VisitorFailure {
		Iterator<ASTNode> it = nodes.iterator();
		while (it.hasNext()) {
			ASTNode node = it.next();
			if (node instanceof FunctionDeclaration) {
				functionDeclaration((FunctionDeclaration) node);
			} else if (node instanceof VariableDeclaration) {
				variableDeclaration((VariableDeclaration) node);
			}
		}
		
		it = nodes.iterator();
		while (it.hasNext()) {
			ASTNode node = it.next();
			if (node instanceof ClassDeclaration) {
				classDeclaration((ClassDeclaration) node);
			}
		}
	}

	public static class SymbolFailure extends VisitorFailure {
		private static final long serialVersionUID = -7055128919685055333L;

		public SymbolFailure(String message) {
			super(message);
		}
	}

	public static class SymbolTypeFailure extends VisitorFailure {
		private static final long serialVersionUID = -7532951546982840176L;

		public SymbolTypeFailure(String message) {
			super(message);
		}

	}
}