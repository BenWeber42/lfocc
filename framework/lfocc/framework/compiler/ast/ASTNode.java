package lfocc.framework.compiler.ast;

import java.util.List;

public interface ASTNode {
	
	public List<ASTNode> getChildren();
	public void extend(Object extension);
	public <T> T extension(Class<T> type);
}
