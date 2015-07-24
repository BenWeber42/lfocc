package lfocc.framework.compiler.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * An ASTIterator from multiple lists.
 */
public class ASTMultiListIterator implements ASTIterator {
	
	// upper level iterator
	private ListIterator<List<ASTNode>> upper = null;
	// lower level iterator
	private ListIterator<ASTNode> lower = null;
	
	public ASTMultiListIterator() {
		List<List<ASTNode>> multilist = new ArrayList<List<ASTNode>>();
		upper = multilist.listIterator();
		if (upper.hasNext()) {
			lower = upper.next().listIterator();
			upper.previous();
		}
	}
	
	public ASTMultiListIterator(List<ASTNode>... lists) {
		List<List<ASTNode>> multilist = new ArrayList<List<ASTNode>>(lists.length);
		
		for (int i = 0; i < lists.length; ++i) {
			if (lists[i] != null)
				multilist.add(lists[i]);
		}
		
		upper = multilist.listIterator();
		if (upper.hasNext()) {
			lower = upper.next().listIterator();
			upper.previous();
		}
	}

	@Override
	public boolean hasNext() {
		if (lower == null)
			return false;
		return hasNext(lower);
	}
	
	private boolean hasNext(ListIterator<ASTNode> nodes) {
		if (nodes.hasNext())
			return true;
		
		if (upper.hasNext()) {
			boolean next = hasNext(upper.next().listIterator());
			upper.previous();
			return next;
		}
		
		return false;
	}

	@Override
	public boolean hasPrevious() {
		if (lower == null)
			return false;
		return hasPrevious(lower);
	}
	
	private boolean hasPrevious(ListIterator<ASTNode> nodes) {
		if (nodes.hasPrevious())
			return true;
		
		if (upper.hasPrevious()) {
			List<ASTNode> list = upper.previous();
			boolean prev = hasPrevious(list.listIterator(list.size()));
			upper.next();
			return prev;
		}
		
		return false;
	}

	@Override
	public ASTNode next() {
		if (lower == null)
			throw new NoSuchElementException();
		if (lower.hasNext())
			return lower.next();
		
		if (!upper.hasNext())
			throw new NoSuchElementException();

		upper.next();

		ASTNode node = null;

		while (upper.hasNext() && node == null) {
			lower = upper.next().listIterator();
			if (lower.hasNext())
				node = lower.next();
		}
		
		upper.previous();
		
		if (node == null)
			throw new NoSuchElementException();
		
		return node;
	}

	@Override
	public ASTNode previous() {
		if (lower == null)
			throw new NoSuchElementException();
		if (lower.hasPrevious())
			return lower.previous();
		
		while (upper.hasPrevious()) {
			List<ASTNode> list = upper.previous();
			lower = list.listIterator(list.size());
			if (lower.hasPrevious())
				return lower.previous();
		}
		
		throw new NoSuchElementException();
	}

	@Override
	public void add(ASTNode node) {
		if (lower == null)
			throw new IllegalStateException();
		
		lower.add(node);

	}

	@Override
	public void remove() {
		if (lower == null)
			throw new IllegalStateException();
		
		lower.remove();
	}

	@Override
	public void set(ASTNode node) {
		if (lower == null)
			throw new IllegalStateException();
		
		lower.set(node);
	}

}
