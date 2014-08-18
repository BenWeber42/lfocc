package lfocc.framework.compiler.ast;

import java.util.List;

public interface ASTNode {
	
	public List<ASTNode> getChildren();
	public <T> void attach(T extension);
	public <T> T extension(Class<T> type);
}
