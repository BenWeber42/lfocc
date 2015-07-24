package lfocc.framework.compiler.ast;

public interface ASTNode {
	
	public void extend(Object extension);
	public <T> T extension(Class<T> type);
	public void accept(ASTVisitor visitor);
}
