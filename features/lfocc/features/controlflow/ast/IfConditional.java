package lfocc.features.controlflow.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.features.expressions.ast.Expression;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ExtendableNode;

public class IfConditional extends ExtendableNode implements ASTNode {

	private Expression expr;
	private List<ASTNode> code;

	public IfConditional(Expression expr, List<ASTNode> code) {
		this.expr = expr;
		this.code = code;
	}
	
	public Expression getExpr() {
		return expr;
	}

	public void setExpr(Expression expr) {
		this.expr = expr;
	}

	public List<ASTNode> getCode() {
		return code;
	}

	public void setCode(List<ASTNode> code) {
		this.code = code;
	}

	@Override
	public List<ASTNode> getChildren() {
		ArrayList<ASTNode> children = new ArrayList<ASTNode>();
		children.add(expr);
		children.addAll(code);
		return children;
	}

}
