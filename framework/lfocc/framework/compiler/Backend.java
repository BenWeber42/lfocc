package lfocc.framework.compiler;

import lfocc.framework.compiler.ast.ASTNode;


public interface Backend {
	// TODO: excpetion in case of failure
	public void generate(StringBuilder output, ASTNode root) throws BackendFailure;
	
	@SuppressWarnings("serial")
	public static class BackendFailure extends Throwable {
		private String message;
		
		public BackendFailure(String message) {
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
