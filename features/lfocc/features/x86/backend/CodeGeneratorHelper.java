package lfocc.features.x86.backend;

public class CodeGeneratorHelper {
	public final static int WORD_SIZE = 4;

	private final static String ESCAPE_STRING = "__";

	public final static String RUNTIME =
			// TODO: testcase for _main function collisions
			// write function
			// TODO: finish runtime
			".text\n" +
			"." + escape("write") + ":\n" +
			"   \n" +
			"   \n" +
			"   \n" +
			"   \n" +
			"   \n" +
			"";


	/**
	 * Marker class to extend AST nodes with so that names don't get escaped by
	 * the backend.
	 */
	public static class NoNameEscape {}
	
	/**
	 * Specifies that the ASTNode should be part of the given namespace
	 */
	public static class NameSpace {
		public final String namespace;
		
		public NameSpace(String namespace) {
			this.namespace = namespace;
		}
	}
	
	/**
	 * Marker class to expose the symbol to the linker (mainly relevant for the
	 * main function/entry point)
	 */
	public static class ExposeLinker {}
	
	public static String escape(String str) {
		return ESCAPE_STRING + str;
	}
}
