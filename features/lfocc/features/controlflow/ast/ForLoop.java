package lfocc.features.controlflow.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.features.expressions.ast.Expression;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ExtendableNode;

public class ForLoop extends ExtendableNode implements ASTNode {

	private List<ASTNode> init;
	private Expression expr;
	private List<ASTNode> repeat;
	private List<ASTNode> code;
	
	public ForLoop(List<ASTNode> init, Expression expr,
			List<ASTNode> repeat, List<ASTNode> code) {
		
		this.init = init;
		this.expr = expr;
		this.repeat = repeat;
		this.code = code;
	}

	public List<ASTNode> getInit() {
		return init;
	}

	public void setInit(List<ASTNode> init) {
		this.init = init;
	}

	public Expression getExpr() {
		return expr;
	}

	public void setExpr(Expression expr) {
		this.expr = expr;
	}

	public List<ASTNode> getRepeat() {
		return repeat;
	}

	public void setRepeat(List<ASTNode> repeat) {
		this.repeat = repeat;
	}

	public List<ASTNode> getCode() {
		return code;
	}

	public void setCode(List<ASTNode> code) {
		this.code = code;
	}

	@Override
	public List<ASTNode> getChildren() {
		ArrayList<ASTNode> children = new ArrayList<ASTNode>(init);
		children.add(expr);
		children.addAll(code);
		children.addAll(repeat);
		return children;
	}

}
