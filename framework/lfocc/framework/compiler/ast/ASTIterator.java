package lfocc.framework.compiler.ast;

/**
 * Very much like ListIterator, just that nextIndex and previousIndex have been
 * omitted.
 */
public interface ASTIterator {
	
	public void add(ASTNode node);
	public boolean hasNext();
	public boolean hasPrevious();
	public ASTNode next();
	public ASTNode previous();
	public void remove();
	public void set(ASTNode node);

}
