package lfocc.features.functions.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.features.expressions.ast.Expression;
import lfocc.features.types.ast.TypeSymbol;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTSequence;
import lfocc.framework.compiler.ast.ASTSlot;
import lfocc.framework.compiler.ast.ExtendableNode;

public class FunctionCall extends ExtendableNode implements Expression {
	
	protected String name;
	protected ASTSlot<ASTSequence> arguments;
	protected FunctionDeclaration declaration;

	public FunctionCall(String name, List<Expression> arguments) {
		this.name = name;
		this.arguments = new ASTSlot<ASTSequence>(new ASTSequence(arguments));
	}

	@Override
	public List<ASTSlot<? extends ASTNode>> getChildren() {
		List<ASTSlot<? extends ASTNode>> children = new ArrayList<ASTSlot<? extends ASTNode>>(1);
		children.add(arguments);
		return children;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ASTSequence getArguments() {
		return arguments.getMember();
	}

	public void setArguments(ASTSequence arguments) {
		this.arguments.setMember(arguments);
	}

	public FunctionDeclaration getDeclaration() {
		return declaration;
	}

	public void setDeclaration(FunctionDeclaration declaration) {
		this.declaration = declaration;
	}

	@Override
	public TypeSymbol getType() {
		assert declaration != null;
		return declaration.getReturnType();
	}

}
