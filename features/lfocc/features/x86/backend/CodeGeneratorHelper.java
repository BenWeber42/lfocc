package lfocc.features.x86.backend;

public class CodeGeneratorHelper {
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
	 * Marker class to expose the symbol to the linker (mainly relevant for the
	 * main function/entry point)
	 */
	public static class ExposeLinker {}
	
	public static String escape(String str) {
		return ESCAPE_STRING + str;
	}
}
