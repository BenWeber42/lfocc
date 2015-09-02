package lfocc.features.x86.backend.generators;

import lfocc.features.base.ast.ScopeKind;
import lfocc.features.variables.ast.Attribute;
import lfocc.features.variables.ast.Variable;
import lfocc.features.variables.ast.VariableDeclaration;
import lfocc.features.x86.backend.CodeGeneratorHelper;

public class VariableCodeGenerator {
	
	private final static String GLOBAL_VARIABLE_ESCAPE = "global_variable__";

	public static String variableDeclaration(VariableDeclaration varDecl) {
		ScopeKind scope = varDecl.extension(ScopeKind.class);
		
		assert scope != null;
		
		if (scope != ScopeKind.GLOBAL)
			// variables as class members, function parameters and function locals
			// are taken care of elsewhere
			return "";
		
		String src = "";
		src += "/**\n";
		src += " * Global variable " + varDecl.getName() + "\n";
		src += " */\n";
		src += ".data\n";
		src += getLabel(varDecl) + ":\n";
		src += "   .long 0x0\n";
		
		return src;
	}
	
	public static String getLabel(VariableDeclaration varDecl) {
		assert varDecl.extension(ScopeKind.class) == ScopeKind.GLOBAL;
		return CodeGeneratorHelper.escape(GLOBAL_VARIABLE_ESCAPE + varDecl.getName());
	}

	public static String attribute(Attribute attribute) {
		String src = "";
		// TODO: implement
		return src;
	}

	public static String variable(Variable attribute) {
		String src = "";
		// TODO: implement
		return src;
	}
}
