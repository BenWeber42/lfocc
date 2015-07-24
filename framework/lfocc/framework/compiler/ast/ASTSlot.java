package lfocc.framework.compiler.ast;

/*
 * This class allows to replace ASTNodes easily for tree rewriting.
 */
public class ASTSlot<T extends ASTNode> {
	
	private T node;
	
	public ASTSlot(T member) {
		this.node = member;
	}
	
	public void set(T member) {
		this.node = member;
	}
	
	public T get() {
		return node;
	}

}
