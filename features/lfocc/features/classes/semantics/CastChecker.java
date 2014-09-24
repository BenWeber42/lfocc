package lfocc.features.classes.semantics;

import lfocc.features.classes.ast.CastExpression;
import lfocc.features.classes.ast.ClassType;
import lfocc.features.classes.ast.NullType;
import lfocc.features.expressions.ast.Expression;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

public class CastChecker extends ASTVisitor {

	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		
		if (node instanceof CastExpression) {
			castExpression((CastExpression) node);
		}
		
		super.visit(node);
	}

	private void castExpression(CastExpression cast) throws ClassTypeFailure {
		
		if (!isClassType(cast.getExpr())) {
			throw new ClassTypeFailure(String.format(
					"Casts are only applicable to class types ('%s' given)!",
					cast.getExpr().getType().getName()));
		}
		
		if (isNullType(cast.getExpr()))
			return;
		
		assert cast.getExpr().getType() instanceof ClassType;
		
		ClassType expr = (ClassType) cast.getExpr().getType();
		
		if (!isParent(cast.getType(), expr) &&
				!isParent(expr, cast.getType())) {
			throw new ClassTypeFailure(String.format(
					"Can't cast from '%s' to '%s' (none is a parent of the other)!",
					expr.getName(), cast.getType().getName()));
		}
		
	}
	
	// TODO: remove code clones (ClassEqualChecker)
	private boolean isClassType(Expression expr) {
		return expr.getType() instanceof ClassType || isNullType(expr);
	}
	
	private boolean isNullType(Expression expr) {
		return expr.getType() instanceof NullType;
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