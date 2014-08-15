package lfocc.features.classes.ast;

import lfocc.features.variables.ast.TypeSymbol;

public class ClassType implements TypeSymbol {

	private String name;
	
	public ClassType(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

}