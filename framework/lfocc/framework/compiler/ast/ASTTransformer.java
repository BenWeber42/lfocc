package lfocc.framework.compiler.ast;

import java.util.Iterator;
import java.util.List;


public abstract class ASTTransformer {
	
	public void transform(List<ASTNode> rootes) throws TransformerFailure {
		Iterator<ASTNode> it = rootes.iterator();
		while (it.hasNext()) {
			visit(it.next());
		}
	}
	
	public void visit(ASTNode node) throws TransformerFailure {
		transform(node.getChildren());
	}
	

	@SuppressWarnings("serial")
	public static class TransformerFailure extends Throwable {
		private String message;
		
		public TransformerFailure(String message) {
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
