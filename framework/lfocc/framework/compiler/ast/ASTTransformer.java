package lfocc.framework.compiler.ast;

import java.util.List;


public interface ASTTransformer {
	
	public void transform(List<ASTNode> rootes) throws TransformerFailure;

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
