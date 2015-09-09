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
	
	public static String getRuntime() {
		String src = "";
		
		// TODO: implement runtime properly
		final String write_fmt = CodeGeneratorHelper.escape("runtime_write_fmt");
		final String read_fmt = CodeGeneratorHelper.escape("runtime_read_fmt");
		final String writef_fmt = CodeGeneratorHelper.escape("runtime_writef_fmt");
		final String readf_fmt = CodeGeneratorHelper.escape("runtime_readf_fmt");
		final String writeln_fmt = CodeGeneratorHelper.escape("runtime_writeln_fmt");

		src += "/**\n";
		src += " * Functions' Runtime:\n";
		src += " */\n";
		src += "\n";
		src += ".data\n";
		src += "\n";
		src += write_fmt + ":\n";
		src += read_fmt + ":\n";
		src += "   .string \"%d\\0\"\n";
		src += "\n";
		src += writef_fmt + ":\n";
		src += readf_fmt + ":\n";
		src += "   .string \"%f\\0\"\n";
		src += "\n";
		src += writeln_fmt + ":\n";
		src += "   .string \"\\n\\0\"\n";
		src += "\n";
		src += ".text\n";
		src += "\n";
		src += getGlobalLabel("write", "") + ":\n";
		src += "   movl -4(%ebp), %eax\n";
		src += "   pushl %ebp\n";
		src += "   movl %esp, %ebp\n";
		src += "   pushl %eax\n";
		src += "   pushl $" + write_fmt + "\n";
		src += "   call printf\n";
		src += "   addl $8, %esp\n";
		src += "   popl %ebp\n";
		src += "   ret\n";
		src += "\n";
		src += getGlobalLabel("writef", "") + ":\n";
		src += "   flds -4(%ebp)\n";
		src += "   pushl %ebp\n";
		src += "   movl %esp, %ebp\n";
		src += "   subl $8, %esp\n";
		src += "   fstpl (%esp)\n";
		src += "   pushl $" + writef_fmt + "\n";
		src += "   call printf\n";
		src += "   addl $12, %esp\n";
		src += "   popl %ebp\n";
		src += "   ret\n";
		src += "\n";
		src += getGlobalLabel("writeln", "") + ":\n";
		src += "   pushl %ebp\n";
		src += "   movl %esp, %ebp\n";
		src += "   pushl $" + writeln_fmt + "\n";
		src += "   call printf\n";
		src += "   addl $4, %esp\n";
		src += "   popl %ebp\n";
		src += "   ret\n";
		src += "\n";
		src += getGlobalLabel("read", "") + ":\n";
		src += "   subl $4, %esp\n";
		src += "   movl %esp, %eax\n";
		src += "   pushl %ebp\n";
		src += "   movl %esp, %ebp\n";
		src += "   pushl %eax\n";
		src += "   pushl $" + read_fmt + "\n";
		src += "   call scanf\n";
		src += "   addl $8, %esp\n";
		src += "   popl %ebp\n";
		src += "   popl %eax\n";
		src += "   ret\n";
		src += "\n";
		src += getGlobalLabel("readf", "") + ":\n";
		src += "   subl $4, %esp\n";
		src += "   movl %esp, %eax\n";
		src += "   pushl %ebp\n";
		src += "   movl %esp, %ebp\n";
		src += "   pushl %eax\n";
		src += "   pushl $" + readf_fmt + "\n";
		src += "   call scanf\n";
		src += "   addl $8, %esp\n";
		src += "   popl %ebp\n";
		src += "   popl %eax\n";
		src += "   ret\n";
		src += "\n";
		src += "\n";

		return src;
	}
	
	public static String functionDeclaration(FunctionDeclaration funcDecl, CodeGeneratorInterface codeGen) throws BackendFailure {
		
		String label = getLabel(funcDecl);
		ScopeKind scope = getScope(funcDecl);
		String src = "";
		
		// TODO: take care of callee-saved registers ebx, esi & edi
		
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
		// allocate space for locals
		src += "   subl $" + funcDecl.extension(FunctionOffsets.class).getSize() + ", %esp\n";

		src += codeGen.dispatch(funcDecl.getChildren());
		
		src += "   movl $0, %eax\n";
		src += "   ret\n\n\n";
		
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
			return getGlobalLabel(funcDecl.getName(), nameSpaceStr);
		else
			return getClassLabel(funcDecl.getName(), nameSpaceStr);
	}
	
	private static String getGlobalLabel(String name, String namespace) {
			return CodeGeneratorHelper.escape(GLOBAL_ESCAPE + namespace + name);
	}
	
	private static String getClassLabel(String name, String namespace) {
			return CodeGeneratorHelper.escape(CLASS_ESCAPE + namespace + name);
	}
	
	public static String returnStatement(ReturnStatement ret, CodeGeneratorInterface codeGen) throws BackendFailure {
		String src = "";
		Expression expr = ret.getExpr();
		if (expr != null) {
			src += codeGen.dispatch(ret.getExpr());
			codeGen.getRegisterManager().free(ReturnRegister.getRegister(ret.getExpr()));
		}
		// TODO: implement
		return src;
	}
	
	public static String functionCall(FunctionCall call, CodeGeneratorInterface codeGen) throws BackendFailure {
		String src = "";
		RegisterManager regs = codeGen.getRegisterManager();
		
		assert call.getDeclaration().extension(ScopeKind.class) == ScopeKind.GLOBAL;

		// TODO: implement properly
		// don't forget about MethodCall!

		Register reg = regs.acquire();
		ReturnRegister.setRegister(call, reg);
		regs.free(reg);
		
		// take care of caller-saved registers (eax, ecx, edx)
		boolean eaxSaved = false;
		if (!regs.isFree(Register.eax)) {
			regs.push(Register.eax);
			eaxSaved = true;
		}
		boolean ecxSaved = false;
		if (!regs.isFree(Register.ecx)) {
			regs.push(Register.ecx);
			ecxSaved = true;
		}
		boolean edxSaved = false;
		if (!regs.isFree(Register.edx)) {
			regs.push(Register.edx);
			edxSaved = true;
		}
		
		// make new call frame
		src += "   push %ebp\n";
		src += "   movl %esp, %ebp\n";
		
		// evaluate arguments
		String argsSrc = "";
		for (Expression arg: call.getArguments()) {
			String _src = "";
			_src += codeGen.dispatch(arg);
			_src += regs.push(ReturnRegister.getRegister(arg));
			argsSrc = _src + argsSrc;
		}
		src += argsSrc;
		
		// do call
		src += "   call " + getLabel(call) + "\n";
		
		// clean up stack
		src += "   addl $" + call.getArguments().size()*CodeGeneratorHelper.WORD_SIZE + ", %esp\n";
		
		// save return value
		if (!call.getDeclaration().getReturnType().getName().equals("void") && reg != Register.eax)
			src += "   movl %eax, %" + reg + "\n";
		
		src += "   popl %ebp\n";

		// pop caller-saved registers
		if (edxSaved)
			regs.pop(Register.edx);
		if (ecxSaved)
			regs.pop(Register.ecx);
		if (eaxSaved)
			regs.pop(Register.eax);
		
		regs.acquire(reg);
		
		if (!call.isExpression())
			regs.free(reg);

		return src;
	}
}
