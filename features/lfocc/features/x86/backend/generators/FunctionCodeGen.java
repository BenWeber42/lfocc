package lfocc.features.x86.backend.generators;

import lfocc.features.base.ast.ScopeKind;
import lfocc.features.functions.ast.FunctionCall;
import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.x86.backend.CodeGeneratorHelper;
import lfocc.features.x86.backend.CodeGeneratorHelper.ExposeLinker;
import lfocc.features.x86.backend.CodeGeneratorHelper.NoNameEscape;
import lfocc.features.x86.backend.preparation.FunctionOffsetGenerator.FunctionOffsets;

public class FunctionCodeGen {
	
	private static final String GLOBAL_ESCAPE = "func__";
	private static final String PROLOGUE =
			"   push %ebp\n" +
			"   mov %ebp, %esp\n";
	private static final String EPILOGUE =
			"   ret\n";
	
	public static String functionDeclaration(FunctionDeclaration funcDecl) {
		
		if (getScope(funcDecl) == ScopeKind.GLOBAL)
			return globalDeclaration(funcDecl);
		else
			return classMemberDeclaration(funcDecl);
	}
	
	private static String globalDeclaration(FunctionDeclaration funcDecl) {
		String src = "";

		src += ".text\n";
		if (funcDecl.extension(ExposeLinker.class) != null)
			src += ".global " + getLabel(funcDecl) + "\n";
		src += getLabel(funcDecl) + ":\n";
		src += PROLOGUE;
		src += "   subl $" + funcDecl.extension(FunctionOffsets.class).getSize() + ", %esp\n";

		// TODO: Function Body
		
		src += EPILOGUE;
		return src;
	}

	private static String classMemberDeclaration(FunctionDeclaration funcDecl) {
		// TODO
		return "";
	}
	
	private static ScopeKind getScope(FunctionDeclaration funcDecl) {
		ScopeKind scopeKind = funcDecl.extension(ScopeKind.class);
		assert scopeKind != null && scopeKind != ScopeKind.LOCAL;
		return scopeKind;
	}
	
	public static String getLabel(FunctionCall funcCall) {
		return getLabel(funcCall.getDeclaration());
	}
	
	public static String getLabel(FunctionDeclaration funcDecl) {
		
		if (funcDecl.extension(NoNameEscape.class) != null)
			return funcDecl.getName();
		
		boolean global = getScope(funcDecl) == ScopeKind.GLOBAL;
		
		if (global)
			return CodeGeneratorHelper.escape(GLOBAL_ESCAPE + funcDecl.getName());
		else
			// TODO
			return "";
	}


}
