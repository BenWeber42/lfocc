package lfocc.features.classes.semantics;

import lfocc.features.classes.ast.ClassType;
import lfocc.features.classes.ast.NullType;
import lfocc.features.expressions.ast.BinaryOperatorExpression;
import lfocc.features.expressions.ast.BinaryOperatorExpression.Operator;
import lfocc.features.expressions.ast.BooleanType;
import lfocc.features.types.ast.TypeSymbol;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

public class ClassEqualChecker extends ASTVisitor {

	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		
		if (node instanceof BinaryOperatorExpression) {
			binaryOperatorExpression((BinaryOperatorExpression) node);
		}
		
		super.visit(node);
		
	}

	private void binaryOperatorExpression(BinaryOperatorExpression binOp) throws ClassTypeFailure {
		
		if (binOp.getOperator() != Operator.EQUAL &&
				binOp.getOperator() != Operator.NOT_EQUAL)
			return;
		
		if (!isClassType(binOp.getLeft().getType())
				&& !isClassType(binOp.getRight().getType()))
			return;
		
		if (!isClassType(binOp.getLeft().getType())
				|| !isClassType(binOp.getRight().getType())) {
			throw new ClassTypeFailure(String.format(
					"Operator '%s' doesn't work for types '%s' && '%s'!",
					binOp.getOperator().getName(),
					binOp.getLeft().getType().getName(),
					binOp.getRight().getType().getName()));
		}
		
		
		if (isNullType(binOp.getLeft().getType()) ||
				isNullType(binOp.getRight().getType())) {
			binOp.setType(new BooleanType());
			return;
		}
		
		assert binOp.getLeft().getType() instanceof ClassType;
		assert binOp.getRight().getType() instanceof ClassType;
		
		ClassType left = (ClassType) binOp.getLeft().getType();
		ClassType right = (ClassType) binOp.getRight().getType();
		
		if (!isParent(left, right) && !isParent(right, left)) {
			throw new ClassTypeFailure(String.format(
					"Classes '%s' and '%s' can't be compared (none of them is a parent of the other)!",
					left.getName(),
					right.getName()));
		}
		
		binOp.setType(new BooleanType());
		
	}
	
	private boolean isClassType(TypeSymbol type) {
		return type instanceof ClassType || isNullType(type);
	}
	
	private boolean isNullType(TypeSymbol type) {
		return type instanceof NullType;
	}
	
	private boolean isParent(ClassType _super, ClassType _sub) {
		while (_sub != null) {
			if (_sub.equals(_super))
				return true;
			_sub = _sub.getParent();
		}
		
		return false;
	}
}