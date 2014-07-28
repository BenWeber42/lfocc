package lfocc.framework.compiler.ast;

import java.util.ArrayList;
import java.util.List;

public class ASTLeafNode implements ASTNode {

	@Override
	public List<ASTNode> getChildren() {
		return new ArrayList<ASTNode>();
	}

}
