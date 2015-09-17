package lfocc.features.x86.backend.generators;

import lfocc.features.expressions.ast.BinaryOperatorExpression;
import lfocc.features.expressions.ast.BooleanConst;
import lfocc.features.expressions.ast.FloatConst;
import lfocc.features.expressions.ast.FloatType;
import lfocc.features.expressions.ast.IntConst;
import lfocc.features.expressions.ast.IntType;
import lfocc.features.expressions.ast.UnaryOperatorExpression;
import lfocc.features.x86.backend.CodeGeneratorHelper.ReturnRegister;
import lfocc.features.x86.backend.CodeGeneratorHelper;
import lfocc.features.x86.backend.CodeGeneratorInterface;
import lfocc.features.x86.backend.LabelManager;
import lfocc.features.x86.backend.RegisterManager;
import lfocc.features.x86.backend.RegisterManager.Register;
import lfocc.framework.compiler.Backend.BackendFailure;

public class ExpressionCodeGenerator {

	public static String intConst(IntConst intConst, RegisterManager regs) {
		String src = "";
		Register reg = regs.acquire();
		src += "   movl $" + intConst.getValue() + ", %" + reg + "\n";
		ReturnRegister.setRegister(intConst, reg);
		return src;
	}
	
	public static String booleanConst(BooleanConst boolConst, RegisterManager regs) {
		String src = "";
		Register reg = regs.acquire();
		src += "   movl $" + ( boolConst.getValue() ? "1" : "0") + ", %" + reg + "\n";
		ReturnRegister.setRegister(boolConst, reg);
		return src;
	}

	public static String floatConst(FloatConst floatConst, RegisterManager regs) {
		String src = "";
		Register reg = regs.acquire();
		src += "   movl $" + Float.floatToRawIntBits(floatConst.getValue()) + ", %" + reg + "\n";
		ReturnRegister.setRegister(floatConst, reg);
		return src;
	}
	
	public static String binaryOperator(BinaryOperatorExpression binOp, CodeGeneratorInterface codeGen) throws BackendFailure {
		String src = "";
		RegisterManager regs = codeGen.getRegisterManager();
		LabelManager labels = codeGen.getLabelManager();
		
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
		
		assert right != null ? ReturnRegister.getRegister(binOp.getRight()) == right : true;
		right = ReturnRegister.getRegister(binOp.getRight());

		// save into left!
		switch (binOp.getOperator()) {
		case PLUS:
		case MINUS:
		case TIMES:
		case DIVIDE:
		case MODULO:

			if (binOp.getRight().getType() instanceof IntType) {
				assert binOp.getLeft().getType() instanceof IntType;
				
				switch (binOp.getOperator()) {
				case PLUS:
					src += "   addl %" + right + ", %" + left + "\n";
					break;
				case MINUS:
					src += "   subl %" + right + ", %" + left + "\n";
					break;
				case TIMES:
					src += "   imul %" + right + ", %" + left + "\n";
					break;
				case DIVIDE:
				case MODULO:
					// establish edx = 0, eax = left, ebx = right
					// without losing other values that might potentially be saved
					// in those registers

					/*
					 * This is a very simple and inefficient solution but serves
					 * the purpose for this proof of concept.
					 */

					// push left & right onto the stack:
					src += "   pushl %" + left + "\n";
					src += "   pushl %" + right + "\n";

					// push eax, ebx & edx onto the stack
					src += "   pushl %eax\n";
					src += "   pushl %ebx\n";
					src += "   pushl %edx\n";

					// edx = 0
					src += "   movl $0, %edx\n";
					// ebx = right
					src += "   movl " + 3*CodeGeneratorHelper.WORD_SIZE + "(%esp), %ebx\n";
					// eax = left
					src += "   movl " + 4*CodeGeneratorHelper.WORD_SIZE + "(%esp), %eax\n";

					// sign extend eax into edx:eax (edx most significant bytes, eax least significant bytes)
					String positive = labels.generateLabel();
					src += "   cmpl $0, %eax\n";
					src += "   jge " + positive + "\n";
					// it's negative, so we need to sign extend it
					src += "   movl $-1, %edx\n";
					src += positive + ":\n";
					src += "   idiv %ebx\n";

					// push result onto stack
					if (binOp.getOperator() == BinaryOperatorExpression.Operator.DIVIDE) {
						src += "   pushl %eax\n"; // quotient goes into eax
					} else {
						assert binOp.getOperator() == BinaryOperatorExpression.Operator.MODULO;
						src += "   pushl %edx\n"; // remainder goes into edx
					}

					// restore registers eax, ebx & edx
					src += "   movl " + 3*CodeGeneratorHelper.WORD_SIZE + "(%esp), %eax\n";
					src += "   movl " + 2*CodeGeneratorHelper.WORD_SIZE + "(%esp), %ebx\n";
					src += "   movl " + 1*CodeGeneratorHelper.WORD_SIZE + "(%esp), %edx\n";

					// restore result
					src += "   movl (%esp), %" + left + "\n";

					// clean up stack
					src += "   addl $" + 6*CodeGeneratorHelper.WORD_SIZE + ", %esp\n";

					break;
				default:
					assert false;
					break;
				
				}
			} else {
				assert binOp.getRight().getType() instanceof FloatType;
				assert binOp.getLeft().getType() instanceof FloatType;

				src += "   pushl %" + right + "\n";
				src += "   flds (%esp)\n";
				src += "   pushl %" + left + "\n";
				src += "   flds (%esp)\n";
				
				switch (binOp.getOperator()) {
				case PLUS:
					src += "   faddp\n";
					break;
				case MINUS:
					src += "   fsubp\n";
					break;
				case TIMES:
					src += "   fmulp\n";
					break;
				case DIVIDE:
					src += "   fdivp\n";
					break;
				default:
					assert false;
				}

				src += "   fstps (%esp)\n";
				src += "   popl %" + left + "\n";
				src += "   addl $" + CodeGeneratorHelper.WORD_SIZE + ", %esp\n";
			}
			break;
		case EQUAL:
		case NOT_EQUAL:
		case GREATER:
		case GREATER_EQUAL:
		case SMALLER:
		case SMALLER_EQUAL:
			String trueLabel = labels.generateLabel();
			String bottomLabel = labels.generateLabel();

			if (binOp.getRight().getType() instanceof FloatType) {
				assert binOp.getLeft().getType() instanceof FloatType;
				// float comparison semantics are a bit different

				src += "   pushl %" + right + "\n";
				src += "   flds (%esp)\n";
				src += "   pushl %" + left + "\n";
				src += "   flds (%esp)\n";
				src += "   addl $" + 2*CodeGeneratorHelper.WORD_SIZE + ", %esp\n";
				src += "   fcomip\n";
				src += "   fstp %st(0)\n";

				switch (binOp.getOperator()) {
				case EQUAL:
					src += "   je " + trueLabel + "\n";
					break;
				case NOT_EQUAL:
					src += "   jne " + trueLabel + "\n";
					break;
				case GREATER:
					src += "   ja " + trueLabel + "\n";
					break;
				case GREATER_EQUAL:
					src += "   jae " + trueLabel + "\n";
					break;
				case SMALLER:
					src += "   jb " + trueLabel + "\n";
					break;
				case SMALLER_EQUAL:
					src += "   jbe " + trueLabel + "\n";
					break;
				default:
					assert false;
					break;
				}
			} else {
				// due to AT&T syntax left and right are swapped!
				src += "   cmpl %" + right + ", %" + left + "\n";

				switch (binOp.getOperator()) {
				case EQUAL:
					src += "   je " + trueLabel + "\n";
					break;
				case NOT_EQUAL:
					src += "   jne " + trueLabel + "\n";
					break;
				case GREATER:
					src += "   jg " + trueLabel + "\n";
					break;
				case GREATER_EQUAL:
					src += "   jge " + trueLabel + "\n";
					break;
				case SMALLER:
					src += "   jl " + trueLabel + "\n";
					break;
				case SMALLER_EQUAL:
					src += "   jle " + trueLabel + "\n";
					break;
				default:
					assert false;
					break;
				}
			}

			src += "   movl $0, %" + left + "\n";
			src += "   jmp " + bottomLabel + "\n";
			src += trueLabel + ":\n";
			src += "   movl $1, %" + left + "\n";
			src += bottomLabel + ":\n";

			break;
		case AND:
			src += "   andl %" + right + ", %" + left + "\n";
			break;
		case OR:
			src += "   orl %" + right + ", %" + left + "\n";
			break;
		default:
			assert false;
			break;
		}
		
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
		
		switch (unOp.getOperator()) {
		case MINUS:
			if (unOp.getExpr().getType() instanceof FloatType) {
				src += "   xorl $0x80000000, %" + reg + "\n";
			} else {
				src += "   negl %" + reg + "\n";
			}
			break;
		case NOT:
			src += "   notl %" + reg + "\n";
			break;
		case PLUS:
			// ignore
			break;
		default:
			assert false;
			break;
		
		}
		
		ReturnRegister.setRegister(unOp, reg);
		
		return src;
	}
}
