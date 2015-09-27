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

		LabelManager labels = codeGen.getLabelManager();
		String bottomLabel = labels.generateLabel();
		
		for (IfConditional ifCond: conditionalSequence.getConditionals()) {
			String nextLabel = labels.generateLabel();

			src += codeGen.dispatch(ifCond.getCondition());
			Register reg = ReturnRegister.getRegister(ifCond.getCondition());
			src += "   cmpl $0, %" + reg + "\n";
			src += "   je " + nextLabel + "\n";
			regs.free(reg);

			src += codeGen.dispatch(ifCond.getCode());
			src += "   jmp " + bottomLabel + "\n";

			src += nextLabel + ":\n";
		}

		if (conditionalSequence.getElse() != null)
			src += codeGen.dispatch(conditionalSequence.getElse().getCode());

		src += bottomLabel + ":\n";
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
		src += "   cmpl $0, %" + reg + "\n";
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
		String bottomLabel = "/* invalid label! */";

		if (loop.getInit() != null)
			src += codeGen.dispatch(loop.getInit());
		
		src += topLabel + ":\n";

		if (loop.getCondition() != null) {
			bottomLabel = labels.generateLabel();
			src += codeGen.dispatch(loop.getCondition());
			Register reg = ReturnRegister.getRegister(loop.getCondition());
			src += "   cmpl $0, %" + reg + "\n";
			src += "   je " + bottomLabel + "\n";
			regs.free(reg);
		}
		
		src += codeGen.dispatch(loop.getCode());

		if (loop.getRepeat() != null)
			src += codeGen.dispatch(loop.getRepeat());

		src += "   jmp " + topLabel + "\n";

		if (loop.getCondition() != null)
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
		src += "   cmpl $1, %" + reg + "\n";
		src += "   je " + topLabel + "\n";
		regs.free(reg);
		
		return src;
	}
}
