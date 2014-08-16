package lfocc.framework.compiler.ast;

import java.util.List;

public interface ASTNode {
	
	public List<ASTNode> getChildren();
	public void attach(Class<?> type, Object extension);
	public Object extension(Class<?> type);
}
