package lfocc.framework.compiler.ast;

import java.util.ArrayList;
import java.util.List;

public class ASTLeafNode extends ExtendableNode implements ASTNode {

	@Override
	public List<ASTNode> getChildren() {
		return new ArrayList<ASTNode>();
	}

}
