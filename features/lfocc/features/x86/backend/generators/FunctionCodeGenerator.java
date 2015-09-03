package lfocc.features.x86.backend.generators;


import lfocc.features.base.ast.ScopeKind;
import lfocc.features.expressions.ast.Expression;
import lfocc.features.functions.ast.FunctionCall;
import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.functions.ast.ReturnStatement;
import lfocc.features.x86.backend.CodeGeneratorHelper;
import lfocc.features.x86.backend.CodeGeneratorHelper.ExposeLinker;
import lfocc.features.x86.backend.CodeGeneratorHelper.NoNameEscape;
import lfocc.features.x86.backend.CodeGeneratorHelper.ReturnRegister;
import lfocc.features.x86.backend.CodeGeneratorInterface;
import lfocc.features.x86.backend.RegisterManager;
import lfocc.features.x86.backend.RegisterManager.Register;
import lfocc.features.x86.backend.CodeGeneratorHelper.NameSpace;
import lfocc.features.x86.backend.preparation.FunctionOffsetGenerator.FunctionOffsets;
import lfocc.framework.compiler.Backend.BackendFailure;

public class FunctionCodeGenerator {
	
	private static final String CLASS_ESCAPE = "class__";
	private static final String GLOBAL_ESCAPE = "func__";
	private static final String PROLOGUE =
			"   push %ebp\n" +
			"   mov %ebp, %esp\n";
	private static final String EPILOGUE =
			"   ret\n";
	
	public static String functionDeclaration(FunctionDeclaration funcDecl, CodeGeneratorInterface codeGen) throws BackendFailure {
		
		String label = getLabel(funcDecl);
		ScopeKind scope = getScope(funcDecl);
		String src = "\n\n";
		
		src += "/**\n";
		if (scope == ScopeKind.GLOBAL)
			src += " * Global function " + funcDecl.getName() + "\n";
		else
			src += " * Class member function " + funcDecl.getName() + "\n";
		src += " */\n";

		src += ".text\n";
		if (funcDecl.extension(ExposeLinker.class) != null)
			src += ".global " + label + "\n";
		src += label + ":\n";
		src += PROLOGUE;
		src += "   subl $" + funcDecl.extension(FunctionOffsets.class).getSize() + ", %esp\n";

		src += codeGen.dispatch(funcDecl.getChildren());
		
		src += EPILOGUE;
		return src;
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
		
		String nameSpaceStr = "";
		
		NameSpace nameSpace;
		if ((nameSpace = funcDecl.extension(NameSpace.class)) != null)
			nameSpaceStr = nameSpace.namespace;

		boolean global = getScope(funcDecl) == ScopeKind.GLOBAL;
		
		if (global)
			return CodeGeneratorHelper.escape(GLOBAL_ESCAPE + nameSpaceStr + funcDecl.getName());
		else
			return CodeGeneratorHelper.escape(CLASS_ESCAPE + nameSpaceStr + funcDecl.getName());
	}
	
	public static String returnStatement(ReturnStatement ret, CodeGeneratorInterface codeGen) throws BackendFailure {
		String src = "";
		Expression expr = ret.getExpr();
		if (expr != null) {
			src += codeGen.dispatch(ret.getExpr());
			codeGen.getRegisterManager().release(ReturnRegister.getRegister(ret.getExpr()));
		}
		// TODO: implement
		return src;
	}
	
	public static String functionCall(FunctionCall call, CodeGeneratorInterface codeGen) {
		String src = "";
		RegisterManager regs = codeGen.getRegisterManager();

		// TODO: implement properly
		// don't forget about MethodCall!
		
		Register reg = regs.acquire();
		src += "   movl $" + 0 + ", %" + reg + "\n";
		ReturnRegister.setRegister(call, reg);
		
		if (!call.isExpression())
			regs.release(reg);

		return src;
	}


}