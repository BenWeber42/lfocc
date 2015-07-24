package lfocc.framework.compiler.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * An ASTSequence is any kind of sequence of AST nodes that implies an order
 * and allows AST nodes to be added and removed.
 * 
 * This class allows adding and removal of AST nodes for tree rewriting.
 */
public class ASTSequence<T extends ASTNode> extends ExtendableNode implements ASTNode {
	
	private List<ASTSlot<T>> children;
	
	public ASTSequence() {
		children = new ArrayList<ASTSlot<T>>();
	}
	
	public ASTSequence(List<? extends T> children) {
		Iterator<? extends T> it = children.iterator();
		while (it.hasNext()) {
			this.children.add(new ASTSlot<T>(it.next()));
		}
	}
	
	public void insert(T node) {
		children.add(0, new ASTSlot<T>(node));
	}
	
	public void add(T node) {
		children.add(new ASTSlot<T>(node));
	}
	
	public void addAll(List<T> nodes) {
		Iterator<T> it = nodes.iterator();
		while (it.hasNext()) {
			children.add(new ASTSlot<T>(it.next()));
		}
	}
	
	@Override
	public List<ASTSlot<T>> getChildren() {
		return children;
	}

}
