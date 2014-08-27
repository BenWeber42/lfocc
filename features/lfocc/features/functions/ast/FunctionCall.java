package lfocc.features.functions.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.features.expressions.ast.Expression;
import lfocc.features.types.ast.TypeSymbol;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ExtendableNode;

public class FunctionCall extends ExtendableNode implements Expression {
	
	private String name;
	private List<Expression> arguments;
	private FunctionDeclaration declaration;

	public FunctionCall(String name, List<Expression> arguments) {
		this.name = name;
		this.arguments = arguments;
	}

	@Override
	public List<ASTNode> getChildren() {
		return new ArrayList<ASTNode>(arguments);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Expression> getArguments() {
		return arguments;
	}

	public void setArguments(List<Expression> arguments) {
		this.arguments = arguments;
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
