package lfocc.features.functions.semantics;

import java.util.List;

import lfocc.features.functions.ast.VoidType;
import lfocc.features.types.semantics.TypeDB;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

/*
 * Adds void type to TypeDB
 */
public class VoidAdder extends ASTVisitor {
	
	public void visit(List<ASTNode> roots) {
		TypeDB.INSTANCE.addType(new VoidType());
	}

}
