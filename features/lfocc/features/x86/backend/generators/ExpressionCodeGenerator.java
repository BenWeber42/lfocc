package lfocc.features.x86.backend.generators;

import lfocc.features.expressions.ast.BinaryOperatorExpression;
import lfocc.features.expressions.ast.BooleanConst;
import lfocc.features.expressions.ast.FloatConst;
import lfocc.features.expressions.ast.IntConst;
import lfocc.features.expressions.ast.UnaryOperatorExpression;
import lfocc.features.x86.backend.CodeGeneratorHelper.ReturnRegister;
import lfocc.features.x86.backend.RegisterManager;
import lfocc.features.x86.backend.RegisterManager.Register;

public class ExpressionCodeGenerator {

	public static String intConst(IntConst intConst, RegisterManager regs) {
		String src = "";
		Register reg = regs.acquireRegister();
		src += "movl $" + intConst.getValue() + ", %" + reg + "\n";
		ReturnRegister.setRegister(intConst, reg);
		return src;
	}
	
	public static String booleanConst(BooleanConst boolConst, RegisterManager regs) {
		String src = "";
		// TODO: implement
		return src;
	}

	public static String floatConst(FloatConst floatConst, RegisterManager regs) {
		String src = "";
		// TODO: implement
		return src;
	}
	
	public static String binaryOperator(BinaryOperatorExpression binOp, RegisterManager regs) {
		String src = "";
		// TODO: implement
		return src;
	}
	
	public static String unaryOperator(UnaryOperatorExpression unOp, RegisterManager regs) {
		String src = "";
		// TODO: implement
		return src;
	}
}
