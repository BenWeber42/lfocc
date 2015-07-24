package lfocc.framework.compiler;

import lfocc.framework.compiler.ast.ASTNode;


public interface Backend {
	// TODO: excpetion in case of failure
	public void generate(StringBuilder output, ASTNode root) throws BackendFailure;
	
	public static class BackendFailure extends Throwable {
		private static final long serialVersionUID = 5414656093100058622L;

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
