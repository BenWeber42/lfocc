package lfocc.features.controlflow.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.features.expressions.ast.Expression;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ExtendableNode;

public class IfConditional extends ExtendableNode implements ASTNode {

	private Expression expr;
	private List<ASTNode> code;
	private ASTNode next;

	public IfConditional(Expression expr, List<ASTNode> code, ASTNode next) {
		this.expr = expr;
		this.code = code;
		this.next = next;
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

	public ASTNode getNext() {
		return next;
	}

	public void setNext(ASTNode next) {
		this.next = next;
	}

	@Override
	public List<ASTNode> getChildren() {
		ArrayList<ASTNode> children = new ArrayList<ASTNode>();
		children.add(expr);
		children.addAll(code);
		if (next != null)
			children.add(next);
		return children;
	}

}
