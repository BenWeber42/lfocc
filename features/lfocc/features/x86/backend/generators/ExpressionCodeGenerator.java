package lfocc.features.x86.backend.generators;

import lfocc.features.expressions.ast.BinaryOperatorExpression;
import lfocc.features.expressions.ast.BooleanConst;
import lfocc.features.expressions.ast.FloatConst;
import lfocc.features.expressions.ast.IntConst;
import lfocc.features.expressions.ast.UnaryOperatorExpression;
import lfocc.features.x86.backend.CodeGeneratorHelper.ReturnRegister;
import lfocc.features.x86.backend.CodeGeneratorInterface;
import lfocc.features.x86.backend.RegisterManager;
import lfocc.features.x86.backend.RegisterManager.Register;
import lfocc.framework.compiler.Backend.BackendFailure;

public class ExpressionCodeGenerator {

	public static String intConst(IntConst intConst, RegisterManager regs) {
		String src = "";
		Register reg = regs.acquire();
		src += "movl $" + intConst.getValue() + ", %" + reg + "\n";
		ReturnRegister.setRegister(intConst, reg);
		return src;
	}
	
	public static String booleanConst(BooleanConst boolConst, RegisterManager regs) {
		String src = "";
		Register reg = regs.acquire();
		src += "movl $" + ( boolConst.getValue() ? "1" : "0") + ", %" + reg + "\n";
		ReturnRegister.setRegister(boolConst, reg);
		return src;
	}

	public static String floatConst(FloatConst floatConst, RegisterManager regs) {
		String src = "";
		Register reg = regs.acquire();
		src += "movl $" + Float.floatToRawIntBits(floatConst.getValue()) + ", %" + reg + "\n";
		ReturnRegister.setRegister(floatConst, reg);
		return src;
	}
	
	public static String binaryOperator(BinaryOperatorExpression binOp, CodeGeneratorInterface codeGen) throws BackendFailure {
		String src = "";
		RegisterManager regs = codeGen.getRegisterManager();
		
		src += codeGen.dispatch(binOp.getLeft());
		Register left = ReturnRegister.getRegister(binOp.getLeft());

		boolean pushed = false;
		Register right = null;
		if (!regs.hasRegister()) {
			right = left == Register.eax ? Register.ebx : Register.eax;
			src += regs.push(right);
			pushed = true;
		}
		
		src += codeGen.dispatch(binOp.getRight());
		
		// TODO: actual code to compute the binary operation
		// save into left!
		
		assert right != null ? ReturnRegister.getRegister(binOp.getRight()) == right : true;

		right = ReturnRegister.getRegister(binOp.getRight());
		regs.free(right);
		
		if (pushed)
			src += regs.pop(right);
		
		ReturnRegister.setRegister(binOp, left);
		
		return src;
	}
	
	public static String unaryOperator(UnaryOperatorExpression unOp, CodeGeneratorInterface codeGen) throws BackendFailure {
		String src = "";
		
		src += codeGen.dispatch(unOp.getExpr());
		
		Register reg = ReturnRegister.getRegister(unOp.getExpr());
		
		// TODO: actual code to compute unary operation
		// safe into reg!
		
		ReturnRegister.setRegister(unOp, reg);
		
		return src;
	}
}
