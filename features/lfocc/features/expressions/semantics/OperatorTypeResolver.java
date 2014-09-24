package lfocc.features.expressions.semantics;

import lfocc.features.expressions.ast.BinaryOperatorExpression;
import lfocc.features.expressions.ast.BooleanType;
import lfocc.features.expressions.ast.Expression;
import lfocc.features.expressions.ast.FloatType;
import lfocc.features.expressions.ast.IntType;
import lfocc.features.expressions.ast.UnaryOperatorExpression;
import lfocc.features.expressions.ast.BinaryOperatorExpression.Operator;
import lfocc.features.functions.ast.VoidType;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

/*
 * Checks and resolves types of operators:
 * - Types of operator arguments are valid
 * - Resolves types of operator expressions
 * - doesn't check equal and not_equal operators if both arguments aren't primitive
 */
public class OperatorTypeResolver extends ASTVisitor {

	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		
		if (node instanceof BinaryOperatorExpression) {
			binOp((BinaryOperatorExpression) node);
		} else if (node instanceof UnaryOperatorExpression) {
			unOp((UnaryOperatorExpression) node);
		} else {
			super.visit(node);
		}
	}

	private void binOp(BinaryOperatorExpression binOp) throws VisitorFailure {
		visit(binOp.getLeft());
		visit(binOp.getRight());
		
		// special care must be taken for these two operators
		// because class types are also applicable for them.
		if (binOp.getOperator() == Operator.EQUAL ||
				binOp.getOperator() == Operator.NOT_EQUAL) {
			
			if (isPrimitive(binOp.getLeft()) != isPrimitive(binOp.getRight())) {
				typeMissmatch(binOp.getOperator(),
						binOp.getLeft(), binOp.getRight());
			}
			
			if (isPrimitive(binOp.getLeft()) || isPrimitive(binOp.getRight())) {
				if (!binOp.getLeft().getType().equals(binOp.getRight().getType())) {
					typeMissmatch(binOp.getOperator(),
							binOp.getLeft(), binOp.getRight());
				}
				
				binOp.setType(new BooleanType());
			}
			
			// This should be in its own ASTVisitor in the functions package
			// But because functions and expressions are so essential to
			// interesting languages it's here for simplicity purposes
			if (isVoid(binOp.getLeft()) || isVoid(binOp.getRight())) {
				typeMissmatch(binOp.getOperator(),
						binOp.getLeft(), binOp.getRight());
			}
			
			return;
		}
		
		if (!binOp.getLeft().getType().equals(binOp.getRight().getType())) {
			typeMissmatch(binOp.getOperator(),
					binOp.getLeft(), binOp.getRight());
		}
		
		switch (binOp.getOperator()) {
		case AND: // fall through
		case OR:
			if (!isBoolean(binOp.getLeft())) {
				throw new OperatorTypeFailure(String.format(
						"Operator '%s' requires boolean types (given '%s' and '%s')!",
						binOp.getOperator().getName(),
						binOp.getLeft().getType().getName(),
						binOp.getRight().getType().getName()));
			}
			
			binOp.setType(new BooleanType());
			
			break;
		case MODULO:
			if (!isInt(binOp.getLeft())) {
				throw new OperatorTypeFailure(String.format(
						"Operator '%s' requires int types (given '%s' and '%s')!",
						binOp.getOperator().getName(),
						binOp.getLeft().getType().getName(),
						binOp.getRight().getType().getName()));
			}
			// fall through
		case PLUS: // fall through
		case MINUS: // fall through
		case TIMES: // fall through
		case DIVIDE:
			if (!isNumeric(binOp.getLeft())) {
				throw new OperatorTypeFailure(String.format(
						"Operator '%s' requires int or float types (given '%s' and '%s'!",
						binOp.getOperator().getName(),
						binOp.getLeft().getType().getName(),
						binOp.getRight().getType().getName()));
			}
			
			binOp.setType(binOp.getLeft().getType());
			
			break;
		case EQUAL:
			assert false; // caught earlier
		case NOT_EQUAL:
			assert false; // caught earlier
		case SMALLER: // fall through
		case SMALLER_EQUAL: // fall through
		case GREATER: // fall through
		case GREATER_EQUAL: // fall through
			if (!isNumeric(binOp.getLeft())) {
				throw new OperatorTypeFailure(String.format(
						"Operator '%s' requires int or float types (given '%s' and '%s')!",
						binOp.getOperator().getName(),
						binOp.getLeft().getType().getName(),
						binOp.getRight().getType().getName()));
			}
			
			binOp.setType(new BooleanType());
			
			break;
		default:
			assert false;
		}
	}

	private void unOp(UnaryOperatorExpression unOp) throws VisitorFailure {
		visit(unOp.getExpr());
		
		switch (unOp.getOperator()) {
		case PLUS: // fall through
		case MINUS:
			if (!isNumeric(unOp.getExpr())) {
				throw new OperatorTypeFailure(String.format(
						"Operator '%s' requires int or float type (given '%s')!",
						unOp.getOperator().getName(),
						unOp.getExpr().getType().getName()));
			}
			
			unOp.setType(unOp.getExpr().getType());
			break;
		case NOT:
			if (!isBoolean(unOp.getExpr())) {
				throw new OperatorTypeFailure(String.format(
						"Operator '%s' requires boolean type (given '%s')!",
						unOp.getOperator().getName(),
						unOp.getExpr().getType().getName()));
			}
			
			unOp.setType(unOp.getExpr().getType());
			break;
		default:
			assert false;
		}
	}
	
	@SuppressWarnings("serial")
	public static class OperatorTypeFailure extends VisitorFailure {

		public OperatorTypeFailure(String message) {
			super(message);
		}
	}
	
	private boolean isPrimitive(Expression expr) {
		return isBoolean(expr) || isInt(expr) || isFloat(expr);
	}
	
	private boolean isNumeric(Expression expr) {
		return isInt(expr) || isFloat(expr);
	}
	
	private boolean isBoolean(Expression expr) {
		return expr.getType() instanceof BooleanType;
	}
	
	private boolean isInt(Expression expr) {
		return expr.getType() instanceof IntType;
	}
	
	private boolean isFloat(Expression expr) {
		return expr.getType() instanceof FloatType;
	}
	
	private boolean isVoid(Expression expr) {
		return expr.getType() instanceof VoidType;
	}
	
	private void typeMissmatch(Operator op, Expression left, Expression right) throws OperatorTypeFailure {
		throw new OperatorTypeFailure(String.format(
				"Operator '%s' doesn't work for types '%s' and '%s'!",
				op.getName(), left.getType().getName(), right.getType().getName()));
	}
}
