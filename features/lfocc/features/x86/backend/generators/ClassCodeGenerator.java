package lfocc.features.x86.backend.generators;

import lfocc.features.classes.ast.CastExpression;
import lfocc.features.classes.ast.ClassDeclaration;
import lfocc.features.classes.ast.ClassType;
import lfocc.features.classes.ast.NewOperator;
import lfocc.features.classes.ast.ThisReference;
import lfocc.features.classes.ast.NullExpression;
import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.types.ast.TypeSymbol;
import lfocc.features.x86.backend.CodeGeneratorHelper;
import lfocc.features.x86.backend.CodeGeneratorHelper.ReturnRegister;
import lfocc.features.x86.backend.CodeGeneratorInterface;
import lfocc.features.x86.backend.LabelManager;
import lfocc.features.x86.backend.RegisterManager;
import lfocc.features.x86.backend.RegisterManager.Register;
import lfocc.features.x86.backend.preparation.ClassPreparer.ClassTable;
import lfocc.features.x86.backend.preparation.ClassPreparer.InstanceTable;
import lfocc.framework.compiler.Backend.BackendFailure;

public class ClassCodeGenerator {

	private static final String CLASS_ESCAPE = "class__";
	
	public static String classDeclaration(ClassDeclaration classDecl, CodeGeneratorInterface codeGen) throws BackendFailure {
		
		String src = "";
		src += "/**\n";
		src += " * Class " + classDecl.getName() + "\n";
		src += " */\n";
		
		src += generateClassTable(classDecl);
		
		src += codeGen.dispatch(classDecl.getChildren());
		
		return src;
	}
	
	private static String generateClassTable(ClassDeclaration classDecl) {

		ClassTable classTable = classDecl.extension(ClassTable.class);

		assert classTable != null && classDecl.extension(InstanceTable.class) != null;

		String src = "";
		
		src += ".data\n";
		src += getLabel(classDecl) + ":\n";
		if (classDecl.getType().getParent() != null)
			src += "   .long " + getLabel(classDecl.getType().getParent().getNode()) + "\n";
		else
			src += "   .long 0x0\n";
		for (FunctionDeclaration func: classTable.getJumpTable())
			src += "   .long " + FunctionCodeGenerator.getLabel(func) + "\n";
		
		src += "\n\n";
		
		return src;
	}
	
	public static String getLabel(ClassDeclaration classDecl) {
		return CodeGeneratorHelper.escape(CLASS_ESCAPE + classDecl.getName());
	}
	
	public static String newOperator(NewOperator newOp, CodeGeneratorInterface codeGen) {
		String src = "";
		RegisterManager regs = codeGen.getRegisterManager();

		Register reg = regs.acquire();
		ReturnRegister.setRegister(newOp, reg);
		regs.free(reg);

		ClassDeclaration clazz = newOp.getType().getNode();
		String classTable = getLabel(clazz);
		int size = clazz.extension(InstanceTable.class).getSize();
		
		// take care of caller-saved registers:
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

		// make new call frame
		src += "   push %ebp\n";
		src += "   movl %esp, %ebp\n";
		
		// do call
		src += "   pushl $" + size + "\n";
		src += "   call malloc\n";
		
		// clean up stack
		src += "   addl $4, %esp\n";
		src += "   popl %ebp\n";
		
		if (reg != Register.eax)
			src += "   movl %eax, %" + reg + "\n";
		
		// pop caller-saved registers
		if (edxSaved)
			src += regs.pop(Register.edx);
		if (ecxSaved)
			src += regs.pop(Register.ecx);
		if (eaxSaved)
			src += regs.pop(Register.eax);
		
		regs.acquire(reg);
		
		// initialize instance table (link to classtable)
		src += "   movl $" + classTable + ", (%" + reg + ")\n";

		return src;
	}
	
	public static String nullExpression(NullExpression nullExpr, RegisterManager regs) {
		String src = "";
		Register reg = regs.acquire();
		src += "   movl $" + 0 + ", %" + reg + "\n";
		ReturnRegister.setRegister(nullExpr, reg);
		return src;
	}
	
	public static String thisReference(ThisReference thisRef, CodeGeneratorInterface codeGen) {
		String src = "";

		Register reg = codeGen.getRegisterManager().acquire();
		ReturnRegister.setRegister(thisRef, reg);

		src += "   movl " + 2*CodeGeneratorHelper.WORD_SIZE + "(%ebp), %" + reg + "\n";

		return src;
	}
	
	public static String castExpression(CastExpression cast, CodeGeneratorInterface codeGen) throws BackendFailure {
		String src = "";
		RegisterManager regs = codeGen.getRegisterManager();
		LabelManager labels = codeGen.getLabelManager();

		src += codeGen.dispatch(cast.getExpr());
		Register reg = ReturnRegister.getRegister(cast.getExpr());
		ReturnRegister.setRegister(cast, reg);

		TypeSymbol expressionType = cast.getExpr().getType();
		assert expressionType instanceof ClassType;
		
		if (((ClassType) expressionType).isParent(cast.getType())) {
			// down casts don't require runtime checks
			return src;
		}

		String bottomLabel = labels.generateLabel();
		
		// null can be cast to any class type
		src += "   cmpl $0, %" + reg + "\n";
		src += "   je " + bottomLabel + "\n";
		
		boolean pushed = false;
		Register castReg;
		if (!regs.hasRegister()) {
			src += regs.push(reg);
			castReg = reg;
			regs.acquire(reg);
			pushed = true;
		} else {
			castReg = regs.acquire();
			src += "   movl %" + reg + ", %" + castReg + "\n";
		}
		
		String castClass = getLabel(cast.getType().getNode());
		
		String topLabel = labels.generateLabel();
		String exitLabel = labels.generateLabel();

		// do cast check
		src += topLabel + ":\n";
		src += "   movl (%" + castReg + "), %" + castReg + "\n";
		src += "   cmpl $" + castClass + ", %" + castReg + "\n";
		src += "   je " + bottomLabel + "\n";
		src += "   cmpl $0, %" + castReg + "\n";
		src += "   je " + exitLabel +"\n";
		src += "   jmp " + topLabel + "\n";
		
		src += exitLabel + ":\n";
		src += "   pushl $-1\n";
		src += "   call exit\n";

		src += bottomLabel + ":\n";
		
		regs.free(castReg);
		if (pushed)
			src += regs.pop(reg);

		return src;
	}
}
