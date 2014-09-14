package lfocc.features.functions.semantics;

import java.util.Iterator;

import lfocc.features.classes.ast.ClassType;
import lfocc.features.classes.ast.NullType;
import lfocc.features.expressions.ast.Expression;
import lfocc.features.functions.ast.FunctionCall;
import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.functions.ast.ReturnStatement;
import lfocc.features.functions.ast.VoidType;
import lfocc.features.types.ast.TypeSymbol;
import lfocc.features.variables.ast.VariableDeclaration;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

/*
 * Checks for (depends on the Types feature):
 * - Correct function calls
 *    - types of arguments
 * - Correct use of return statements
 *    - correct types of return expressions
 */
public class FunctionTypeChecker extends ASTVisitor {

	TypeSymbol currentReturnType = null;
	
	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		if (node instanceof FunctionDeclaration) {
			functionDeclaration((FunctionDeclaration) node);
		} else if (node instanceof ReturnStatement) {
			returnStatement((ReturnStatement) node);
		} else if (node instanceof FunctionCall) {
			functionCall((FunctionCall) node);
		} else {
			visit(node.getChildren());
		}
	}

	private void functionCall(FunctionCall node) throws VisitorFailure {
		
		Iterator<VariableDeclaration> params = node.getDeclaration().getParameters().iterator();
		Iterator<Expression> args = node.getArguments().iterator();
		
		while (params.hasNext()) {
			assert args.hasNext();
			
			TypeSymbol decl = params.next().getType();
			TypeSymbol expr = args.next().getType();
			
			if (!compatible(decl, expr)) {
				throw new ReturnFailure(String.format(
						"Argument type '%s' mismatches declared parameter type '%s'!",
						expr.getName(),
						decl.getName()
						));
			}
		}
		
		assert !args.hasNext();
		
		visit(node.getChildren());
	}

	private void returnStatement(ReturnStatement ret) throws VisitorFailure {
		
		if (currentReturnType.equals(new VoidType()) && ret.getExpr() != null) {
			throw new ReturnFailure(String.format(
					"Return type '%s' mismatches declared return type '%s'!",
					ret.getExpr().getType().getName(),
					currentReturnType.getName()));
		}
		
		if (!currentReturnType.equals(new VoidType()) && ret.getExpr() == null) {
			throw new ReturnFailure("Empty return statement in non-void function!");
		}

		if (!currentReturnType.equals(new VoidType()) &&
				!compatible(currentReturnType, ret.getExpr().getType())) {
			throw new ReturnFailure(String.format(
					"Return type '%s' mismatches declared return type '%s'!",
					ret.getExpr().getType().getName(),
					currentReturnType.getName()));
		}
		
		visit(ret.getChildren());
	}
	
	private boolean compatible(TypeSymbol decl, TypeSymbol expr) {
		if (!(decl instanceof ClassType))
			return decl.equals(expr);
		
		if (expr instanceof NullType)
			return true;
		
		if (!(expr instanceof ClassType))
			return false;
		
		ClassType _decl = (ClassType) decl;
		ClassType _expr = (ClassType) expr;
		
		while (_expr != null) {
			if (_decl.equals(_expr))
				return true;
			
			_expr = _expr.getParent();
		}
		
		return false;
			
	}

	private void functionDeclaration(FunctionDeclaration func) throws VisitorFailure {
		currentReturnType = func.getReturnType();
		visit(func.getChildren());
		currentReturnType = null;
	}
}
