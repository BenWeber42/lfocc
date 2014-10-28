package lfocc.features.expressions.semantics;

import lfocc.features.expressions.ast.BooleanType;
import lfocc.features.expressions.ast.FloatType;
import lfocc.features.expressions.ast.IntType;
import lfocc.features.types.semantics.TypeDB;
import lfocc.framework.compiler.ast.ASTVisitor;

/*
 * Registers all primitive types (int, float, boolean) into the TypeDB
 */
public class Primitives extends ASTVisitor {

	public Primitives() {
		TypeDB.INSTANCE.addType(new IntType());
		TypeDB.INSTANCE.addType(new FloatType());
		TypeDB.INSTANCE.addType(new BooleanType());
	}
}
