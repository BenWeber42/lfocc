package lfocc.features.functions.semantics;

import java.util.List;

import lfocc.features.functions.ast.VoidType;
import lfocc.features.types.semantics.TypeDB;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTTransformer;

public class VoidAdder extends ASTTransformer {
	
	public void transform(List<ASTNode> roots) {
		TypeDB.INSTANCE.addType(new VoidType());
	}

}
