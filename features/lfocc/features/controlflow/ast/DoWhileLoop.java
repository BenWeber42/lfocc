package lfocc.features.controlflow.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lfocc.features.expressions.ast.Expression;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ExtendableNode;

public class DoWhileLoop extends ExtendableNode implements ASTNode {

	private Expression expr;
	private List<ASTNode> code;
	
	public DoWhileLoop(List<ASTNode> code, Expression expr) {
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
		ArrayList<ASTNode> children = new ArrayList<ASTNode>(Arrays.asList(expr));
		children.addAll(code);
		return children;
	}

}
