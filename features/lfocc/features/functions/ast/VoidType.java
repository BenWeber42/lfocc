package lfocc.features.functions.ast;

import lfocc.features.variables.ast.TypeSymbol;

public class VoidType implements TypeSymbol {

	@Override
	public String getName() {
		return "void";
	}

}
