package lfocc.features.functions.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.features.types.ast.TypeSymbol;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ExtendableNode;

public class FunctionDeclaration extends ExtendableNode implements ASTNode {

	private String name;
	private TypeSymbol returnType;
	private List<ASTNode> parameters;
	private List<ASTNode> code;
	
	public FunctionDeclaration(TypeSymbol returnType, String name, List<ASTNode> parameters, List<ASTNode> code) {
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

	public TypeSymbol getReturnType() {
		return returnType;
	}

	public void setReturnType(TypeSymbol returnType) {
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
