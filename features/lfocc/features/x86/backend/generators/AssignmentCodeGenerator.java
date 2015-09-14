package lfocc.features.x86.backend.generators;

import lfocc.features.assignments.ast.Assignment;
import lfocc.features.x86.backend.CodeGeneratorInterface;
import lfocc.features.x86.backend.RegisterManager;
import lfocc.features.x86.backend.RegisterManager.Register;
import lfocc.features.x86.backend.CodeGeneratorHelper.ReturnRegister;
import lfocc.framework.compiler.Backend.BackendFailure;

public class AssignmentCodeGenerator {

	public static String assignment(Assignment assign, CodeGeneratorInterface codeGen) throws BackendFailure {
		String src = "";
		RegisterManager regs = codeGen.getRegisterManager();
		
		src += codeGen.getAddress(assign.getTarget());
		src += codeGen.dispatch(assign.getValue());

		Register target = ReturnRegister.getRegister(assign.getTarget());
		Register value = ReturnRegister.getRegister(assign.getValue());
		
		src += "   movl %" + value + ", (%" + target + ")\n";
		
		regs.free(target);
		regs.free(value);

		return src;
	}
}
