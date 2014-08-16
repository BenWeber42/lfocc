package lfocc.features.functions.ast;

import java.util.ArrayList;
import java.util.List;

import lfocc.features.types.ast.TypeSymbol;
import lfocc.features.variables.ast.VariableDeclaration;

public class FunctionSymbol {

	private FunctionDeclaration node;
	private List<VariableDeclaration> locals = new ArrayList<VariableDeclaration>();
	private List<VariableDeclaration> parameters = new ArrayList<VariableDeclaration>();
	private TypeSymbol returnType;
	
	public FunctionSymbol(FunctionDeclaration node) {
		this.node = node;
	}

	public List<VariableDeclaration> getLocals() {
		return locals;
	}

	public void setLocals(List<VariableDeclaration> locals) {
		this.locals = locals;
	}

	public List<VariableDeclaration> getParameters() {
		return parameters;
	}

	public void setParameters(List<VariableDeclaration> parameters) {
		this.parameters = parameters;
	}

	public TypeSymbol getReturnType() {
		return returnType;
	}

	public void setReturnType(TypeSymbol returnType) {
		this.returnType = returnType;
	}

	public FunctionDeclaration getNode() {
		return node;
	}

}
