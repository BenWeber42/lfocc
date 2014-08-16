package lfocc.features.controlflow.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ExtendableNode;

public class ElseConditional extends ExtendableNode implements ASTNode {
	
	private List<ASTNode> code;

	public ElseConditional(List<ASTNode> code) {
		this.code = code;
	}

	public List<ASTNode> getCode() {
		return code;
	}

	public void setCode(List<ASTNode> code) {
		this.code = code;
	}

	@Override
	public List<ASTNode> getChildren() {
		return new ArrayList<ASTNode>(code);
	}

}
