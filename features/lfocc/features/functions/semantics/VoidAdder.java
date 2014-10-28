package lfocc.features.functions.semantics;

import lfocc.features.functions.ast.VoidType;
import lfocc.features.types.semantics.TypeDB;
import lfocc.framework.compiler.ast.ASTVisitor;

/*
 * Adds void type to TypeDB
 */
public class VoidAdder extends ASTVisitor {
	
	public VoidAdder() {
		TypeDB.INSTANCE.addType(new VoidType());
	}

}
