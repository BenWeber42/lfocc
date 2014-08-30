package lfocc.features.types.semantics;

import java.util.Stack;

import lfocc.features.classes.ast.ClassDeclaration;
import lfocc.features.classes.ast.ClassType;
import lfocc.features.functions.ast.FunctionCall;
import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.functions.ast.FunctionScope;
import lfocc.features.functions.ast.MethodCall;
import lfocc.features.variables.ast.Attribute;
import lfocc.features.variables.ast.Variable;
import lfocc.features.variables.ast.VariableDeclaration;
import lfocc.features.variables.ast.VariableScope;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

/*
 * Originally I wanted to do variable resolution, method resolution and type
 * checking in distinct stages. But there are a few snippets that don't allow
 * any orderings of variable resolution, method resolution and type checking:
 * 
 * 1) var.method(); // The method can't be resolved before the variable's type is known
 * -> first do variable resolution, then do method resolution
 * 
 * 2) method().attribute; // the attribute can't be resolved before the method's return type is known
 * -> first do method resolution, then do variable resolution
 * 
 * The first two examples don't allow any ordering for variable & method resolution
 * in two distinct stages. (A fixed point iteration would be possible, but
 * potentially very slow)
 * 
 * 3) (cast<Base> derived).method()); // The method can't be resolved before the expression's type is known
 * -> first resolve the expression's type, then do method resolution
 * 
 * 4) (method() + 5); // The expression's type can't be resolved before the method's return type is known
 * -> first resolve the method, then do the type checking on the expression
 * 
 * These two examples don't allow any ordering for expression type checking
 * and method/variable resolution in two distinct stages.
 * (Again a fixed point iteration would be possible though with the same drawback)
 * 
 * So these semantic checks should probably occur at the same time and allow
 * interleaving. To keep it simple they're put into this class. It's static
 * so there are static dependencies which isn't so good. But having it dynamic
 * would be quite tedious and wouldn't allow for more 'interesting' flexibility
 * in the sense that it wouldn't allow to generate interesting languages.
 * (I don't consider languages without expressions, variables or functions
 * very interesting and I don't want to focus on them.)
 */
public class SymbolResolver extends ASTVisitor {

	private Stack<VariableScope> variables = new Stack<VariableScope>();
	private Stack<FunctionScope> functions = new Stack<FunctionScope>();
	
	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		
		if (node instanceof FunctionCall) {
			functionCall((FunctionCall) node);
		} else if (node instanceof MethodCall) {
			methodCall((MethodCall) node);
		} else if (node instanceof Variable) {
			variable((Variable) node);
		} else if (node instanceof Attribute) {
			attribute((Attribute) node);
		} else {
			
			if (node.extension(FunctionScope.class) != null) {
				functions.push(node.extension(FunctionScope.class));
			}

			if (node.extension(VariableScope.class) != null) {
				variables.push(node.extension(VariableScope.class));
			}
			
			visit(node.getChildren());
			
			if (node.extension(FunctionScope.class) != null) {
				functions.pop();
			}
			
			if (node.extension(VariableScope.class) != null) {
				variables.pop();
			}
			
		}
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
	
	@SuppressWarnings("serial")
	public static class SymbolFailure extends VisitorFailure {

		public SymbolFailure(String message) {
			super(message);
		}
	}

	@SuppressWarnings("serial")
	public static class SymbolTypeFailure extends VisitorFailure {

		public SymbolTypeFailure(String message) {
			super(message);
		}

	}
}