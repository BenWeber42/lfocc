package lfocc.features.x86.backend.generators;

import lfocc.features.base.ast.ScopeKind;
import lfocc.features.expressions.ast.Expression;
import lfocc.features.functions.ast.FunctionCall;
import lfocc.features.functions.ast.MethodCall;
import lfocc.features.x86.backend.CodeGeneratorHelper;
import lfocc.features.x86.backend.CodeGeneratorHelper.ReturnRegister;
import lfocc.features.x86.backend.CodeGeneratorInterface;
import lfocc.features.x86.backend.RegisterManager;
import lfocc.features.x86.backend.RegisterManager.Register;
import lfocc.features.x86.backend.preparation.ClassPreparer.MethodOffset;
import lfocc.framework.compiler.Backend.BackendFailure;

public class MethodCodeGenerator {

	public static String functionCall(FunctionCall call, CodeGeneratorInterface codeGen) throws BackendFailure {

		String src = "";
		RegisterManager regs = codeGen.getRegisterManager();
		
		assert call.getDeclaration().extension(ScopeKind.class) == ScopeKind.CLASS_MEMBER;
		
		// TODO: 16 byte stack alignment?

		Register reg = regs.acquire();
		ReturnRegister.setRegister(call, reg);
		regs.free(reg);
		
		// take care of caller-saved registers (eax, ecx, edx)
		boolean eaxSaved = false;
		if (!regs.isFree(Register.eax)) {
			src += regs.push(Register.eax);
			eaxSaved = true;
		}
		boolean ecxSaved = false;
		if (!regs.isFree(Register.ecx)) {
			src += regs.push(Register.ecx);
			ecxSaved = true;
		}
		boolean edxSaved = false;
		if (!regs.isFree(Register.edx)) {
			src += regs.push(Register.edx);
			edxSaved = true;
		}
		
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
		Register address;
		if (call instanceof MethodCall) {
			MethodCall method = (MethodCall) call;
			src += codeGen.dispatch(method.getExpr());
			address = ReturnRegister.getRegister(method.getExpr());
		} else {
			address = regs.acquire();
			src += "   movl " + 2*CodeGeneratorHelper.WORD_SIZE + "(%ebp), %" + address + "\n";
		}
		
		assert !regs.isFree(address);
		src += "   pushl %" + address + "\n";

		
		// address now contains the reference to the instance table
		src += "   movl (%" + address + "), %" + address + "\n";
		// address now contains the reference to the class table
		src += "   movl " + MethodOffset.getOffset(call.getDeclaration()) + "(%" + address + "), %" + address + "\n";
		// now we have the address of the target
		src += "   call %" + address + "\n";
		
		regs.free(address);

		// clean up stack
		int argumentsSize = (1 + call.getArguments().size())*CodeGeneratorHelper.WORD_SIZE;
		src += "   addl $" + argumentsSize + ", %esp\n";
		
		// save return value
		if (!call.getDeclaration().getReturnType().getName().equals("void") && reg != Register.eax)
			src += "   movl %eax, %" + reg + "\n";
		
		// pop caller-saved registers
		if (edxSaved)
			src += regs.pop(Register.edx);
		if (ecxSaved)
			src += regs.pop(Register.ecx);
		if (eaxSaved)
			src += regs.pop(Register.eax);
		
		regs.acquire(reg);
		
		if (!call.isExpression())
			regs.free(reg);

		return src;
	}
}
