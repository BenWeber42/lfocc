package lfocc.features.x86.backend.generators;

import lfocc.features.controlflow.ast.ConditionalSequence;
import lfocc.features.controlflow.ast.IfConditional;
import lfocc.features.controlflow.ast.DoWhileLoop;
import lfocc.features.controlflow.ast.ForLoop;
import lfocc.features.controlflow.ast.WhileLoop;
import lfocc.features.x86.backend.CodeGeneratorInterface;
import lfocc.features.x86.backend.RegisterManager;
import lfocc.features.x86.backend.CodeGeneratorHelper.ReturnRegister;
import lfocc.framework.compiler.Backend.BackendFailure;

public class ControlFlowCodeGenerator {

	public static String conditionalSequence(ConditionalSequence conditionalSequence, CodeGeneratorInterface codeGen) throws BackendFailure {
		String src = "";
		RegisterManager regs = codeGen.getRegisterManager();
		
		for (IfConditional ifCond: conditionalSequence.getConditionals()) {
			src += codeGen.dispatch(ifCond.getCondition());
			regs.release(ReturnRegister.getRegister(ifCond.getCondition()));
			src += codeGen.dispatch(ifCond.getCode());
		}

		if (conditionalSequence.getElse() != null)
			src += codeGen.dispatch(conditionalSequence.getElse().getCode());

		// TODO: implement
		return src;
	}

	public static String whileLoop(WhileLoop loop, CodeGeneratorInterface codeGen) throws BackendFailure {
		String src = "";
		RegisterManager regs = codeGen.getRegisterManager();
		
		// TODO: implement
		
		src += codeGen.dispatch(loop.getCondition());
		regs.release(ReturnRegister.getRegister(loop.getCondition()));
		
		codeGen.dispatch(loop.getCode());
		return src;
	}

	public static String forLoop(ForLoop loop, CodeGeneratorInterface codeGen) throws BackendFailure {
		String src = "";
		RegisterManager regs = codeGen.getRegisterManager();

		// TODO: implement
		
		src += codeGen.dispatch(loop.getCondition());
		regs.release(ReturnRegister.getRegister(loop.getCondition()));
		
		codeGen.dispatch(loop.getCode());
		return src;
	}

	public static String doWhileLoop(DoWhileLoop loop, CodeGeneratorInterface codeGen) throws BackendFailure {
		String src = "";
		RegisterManager regs = codeGen.getRegisterManager();

		// TODO: implement
		
		src += codeGen.dispatch(loop.getCondition());
		regs.release(ReturnRegister.getRegister(loop.getCondition()));
		
		codeGen.dispatch(loop.getCode());
		return src;
	}
}
