package lfocc.features.x86.backend.generators;

import lfocc.features.controlflow.ast.ConditionalSequence;
import lfocc.features.controlflow.ast.IfConditional;
import lfocc.features.controlflow.ast.DoWhileLoop;
import lfocc.features.controlflow.ast.ForLoop;
import lfocc.features.controlflow.ast.WhileLoop;
import lfocc.features.x86.backend.CodeGeneratorInterface;
import lfocc.features.x86.backend.LabelManager;
import lfocc.features.x86.backend.RegisterManager;
import lfocc.features.x86.backend.RegisterManager.Register;
import lfocc.features.x86.backend.CodeGeneratorHelper.ReturnRegister;
import lfocc.framework.compiler.Backend.BackendFailure;

public class ControlFlowCodeGenerator {

	public static String conditionalSequence(ConditionalSequence conditionalSequence, CodeGeneratorInterface codeGen) throws BackendFailure {
		String src = "";
		RegisterManager regs = codeGen.getRegisterManager();
		
		for (IfConditional ifCond: conditionalSequence.getConditionals()) {
			src += codeGen.dispatch(ifCond.getCondition());
			regs.free(ReturnRegister.getRegister(ifCond.getCondition()));
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

		LabelManager labels = codeGen.getLabelManager();
		String topLabel = labels.generateLabel();
		String bottomLabel = labels.generateLabel();

		src += topLabel + ":\n";

		src += codeGen.dispatch(loop.getCondition());
		Register reg = ReturnRegister.getRegister(loop.getCondition());
		src += "   cmpl %" + reg + ", $0\n";
		src += "   je " + bottomLabel + "\n";
		regs.free(reg);
		
		src += codeGen.dispatch(loop.getCode());

		src += "   jmp " + topLabel + "\n";
		src += bottomLabel + ":\n";

		return src;
	}

	public static String forLoop(ForLoop loop, CodeGeneratorInterface codeGen) throws BackendFailure {
		String src = "";
		RegisterManager regs = codeGen.getRegisterManager();

		LabelManager labels = codeGen.getLabelManager();
		String topLabel = labels.generateLabel();
		String bottomLabel = labels.generateLabel();

		src += codeGen.dispatch(loop.getInit());
		
		src += topLabel + ":\n";

		src += codeGen.dispatch(loop.getCondition());
		Register reg = ReturnRegister.getRegister(loop.getCondition());
		src += "   cmpl %" + reg + ", $0\n";
		src += "   je " + bottomLabel + "\n";
		regs.free(reg);
		
		src += codeGen.dispatch(loop.getCode());
		src += codeGen.dispatch(loop.getRepeat());

		src += "   jmp " + topLabel + "\n";
		src += bottomLabel + ":\n";

		return src;
	}

	public static String doWhileLoop(DoWhileLoop loop, CodeGeneratorInterface codeGen) throws BackendFailure {
		String src = "";
		RegisterManager regs = codeGen.getRegisterManager();

		LabelManager labels = codeGen.getLabelManager();
		String topLabel = labels.generateLabel();

		src += topLabel + ":\n";
		src += codeGen.dispatch(loop.getCode());

		src += codeGen.dispatch(loop.getCondition());
		Register reg = ReturnRegister.getRegister(loop.getCondition());
		src += "   cmpl %" + reg + ", $1\n";
		src += "   je " + topLabel + "\n";
		regs.free(reg);
		
		return src;
	}
}
