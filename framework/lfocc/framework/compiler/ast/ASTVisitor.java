package lfocc.framework.compiler.ast;


public abstract class ASTVisitor {
	
	public void transform(ASTNode node) {
		visit(node);
	}
	
	protected <T extends ASTNode> void visit(T node) {
		node.accept(this);
	}
	
	protected <T extends ASTNode> void visit(ASTSlot<T> slot, T node) {
		visit(node);
	}
	
	public void finish() throws VisitorFailure {
		
	}

	@SuppressWarnings("serial")
	public static class VisitorFailure extends Throwable {
		private String message;
		
		public VisitorFailure(String message) {
			setMessage(message);
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
}
