package lfocc.features.x86.backend;

import lfocc.features.expressions.ast.Expression;
import lfocc.features.x86.backend.RegisterManager.Register;

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

	/**
	 * Helper class to determine into which register an expression will be
	 * evaluated.
	 */
	public static class ReturnRegister {
		public final Register reg;

		private ReturnRegister(Register reg) {
			this.reg = reg;
		}
	
		public static void setRegister(Expression expr, Register reg) {
			expr.extend(new ReturnRegister(reg));
		}

		public static Register getRegister(Expression expr) {
			ReturnRegister reg = expr.extension(ReturnRegister.class);

			assert reg != null;

			return reg.reg;
		}
		
		public static boolean hasRegister(Expression expr) {
			return expr.extension(Register.class) != null;
		}
	}
}
