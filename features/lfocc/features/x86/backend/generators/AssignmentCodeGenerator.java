package lfocc.features.x86.backend.generators;

import lfocc.features.assignments.ast.Assignment;
import lfocc.features.x86.backend.CodeGeneratorInterface;
import lfocc.features.x86.backend.RegisterManager;
import lfocc.features.x86.backend.CodeGeneratorHelper.ReturnRegister;
import lfocc.framework.compiler.Backend.BackendFailure;

public class AssignmentCodeGenerator {

	public static String assignment(Assignment assign, CodeGeneratorInterface codeGen) throws BackendFailure {
		String src = "";
		RegisterManager regs = codeGen.getRegisterManager();
		
		src += codeGen.getAddress(assign.getTarget());
		src += codeGen.dispatch(assign.getValue());

		// TODO: implement
		
		regs.release(ReturnRegister.getRegister(assign.getTarget()));
		regs.release(ReturnRegister.getRegister(assign.getValue()));
		return src;
	}
}
