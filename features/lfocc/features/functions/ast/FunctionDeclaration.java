package lfocc.features.functions.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.features.types.ast.TypeSymbol;
import lfocc.features.variables.ast.VariableDeclaration;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTSlot;
import lfocc.framework.compiler.ast.ASTSequence;
import lfocc.framework.compiler.ast.ExtendableNode;

public class FunctionDeclaration extends ExtendableNode implements ASTNode {

	private String name;
	private TypeSymbol returnType;
	private ASTSlot<ASTSequence> parameters;
	private ASTSlot<ASTSequence> code;
	
	public FunctionDeclaration(TypeSymbol returnType, String name, List<VariableDeclaration> parameters, List<ASTNode> code) {
		this.returnType = returnType;
		this.name = name;
		this.parameters = new ASTSlot<ASTSequence>(new ASTSequence(parameters));
		this.code = new ASTSlot<ASTSequence>(new ASTSequence(code));
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

	public ASTSequence getParameters() {
		return parameters.getMember();
	}

	public void setParameters(ASTSequence parameters) {
		this.parameters.setMember(parameters);
	}

	public ASTSequence getCode() {
		return code.getMember();
	}

	public void setCode(ASTSequence code) {
		this.code.setMember(code);
	}

	@Override
	public List<ASTSlot<? extends ASTNode>> getChildren() {
		List<ASTSlot<? extends ASTNode>> children = new ArrayList<ASTSlot<? extends ASTNode>>(2);
		children.add(parameters);
		children.add(code);
		return children;
	}

}
