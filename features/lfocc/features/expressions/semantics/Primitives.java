package lfocc.features.expressions.semantics;

import java.util.List;

import lfocc.features.expressions.ast.BooleanType;
import lfocc.features.expressions.ast.FloatType;
import lfocc.features.expressions.ast.IntType;
import lfocc.features.types.semantics.TypeDB;
import lfocc.framework.compiler.ast.ASTTransformer;
import lfocc.framework.compiler.ast.ASTNode;

public class Primitives extends ASTTransformer {

	public void transform(List<ASTNode> roots) {
		TypeDB.INSTANCE.addType(new IntType());
		TypeDB.INSTANCE.addType(new FloatType());
		TypeDB.INSTANCE.addType(new BooleanType());
	}
}
