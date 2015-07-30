package lfocc.features.x86.backend;

import lfocc.features.globalscope.ast.GlobalScope;

public interface CodeGeneratorInterface {
	final static String ESCAPE_STRING = "__";

	final static String RUNTIME =
			// TODO: testcase for _main function collisions
			// write function
			// TODO: finish runtime
			".text\n" +
			"." + ESCAPE_STRING + "write:\n" +
			"   \n" +
			"   \n" +
			"   \n" +
			"   \n" +
			"   \n" +
			"";
	
	public String generate(GlobalScope root);
	
	
	/**
 	* Marker class to extend AST nodes with so that names don't get escaped by
 	* the backend.
 	*/
	static public class NoNameEscape {}
	// TODO: escaping
}
