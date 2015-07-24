package lfocc.framework.compiler.ast;

import java.util.Iterator;
import java.util.List;


public abstract class ASTVisitor {
	
	public void visit(List<ASTNode> nodes) throws VisitorFailure {
		if (nodes == null)
			return;
		
		Iterator<ASTNode> it = nodes.iterator();
		while (it.hasNext()) {
			visit(it.next());
		}
	}
	
	public void visit(ASTNode node) throws VisitorFailure {
		visit(node.getChildren());
	}
	
	public void finish() throws VisitorFailure {
		
	}

	public static class VisitorFailure extends Throwable {
		private static final long serialVersionUID = 8460878448764858578L;

		private String message;
		
		public VisitorFailure(String message) {
			this.setMessage(message);
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
}
