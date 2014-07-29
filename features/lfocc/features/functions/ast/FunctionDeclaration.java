package lfocc.features.functions.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.framework.compiler.ast.ASTNode;

public class FunctionDeclaration implements ASTNode {

	private String name;
	private String returnType;
	private List<ASTNode> parameters;
	private List<ASTNode> code;
	
	public FunctionDeclaration(String returnType, String name, List<ASTNode> parameters, List<ASTNode> code) {
		this.returnType = returnType;
		this.name = name;
		this.parameters = parameters;
		this.code = code;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public List<ASTNode> getParameters() {
		return parameters;
	}

	public void setParameters(List<ASTNode> parameters) {
		this.parameters = parameters;
	}

	public List<ASTNode> getCode() {
		return code;
	}

	public void setCode(List<ASTNode> code) {
		this.code = code;
	}

	@Override
	public List<ASTNode> getChildren() {
		List<ASTNode> children = new ArrayList<ASTNode>(parameters);
		children.addAll(code);
		return children;
	}

}
