package lfocc.features.x86.backend.generators;

import lfocc.features.classes.ast.CastExpression;
import lfocc.features.classes.ast.ClassDeclaration;
import lfocc.features.classes.ast.NewOperator;
import lfocc.features.classes.ast.ThisReference;
import lfocc.features.classes.ast.NullExpression;
import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.x86.backend.CodeGeneratorHelper;
import lfocc.features.x86.backend.CodeGeneratorHelper.ReturnRegister;
import lfocc.features.x86.backend.CodeGeneratorInterface;
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
			regs.push(Register.eax);
			eaxSaved = true;
		}
		boolean ecxSaved = false;
		if (!regs.isFree(Register.ecx)) {
			regs.push(Register.ecx);
			ecxSaved = true;
		}
		boolean edxSaved = false;
		if (!regs.isFree(Register.edx)) {
			regs.push(Register.edx);
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
			regs.pop(Register.edx);
		if (ecxSaved)
			regs.pop(Register.ecx);
		if (eaxSaved)
			regs.pop(Register.eax);
		
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
	
	public static String thisReference(ThisReference thisRef, ThisOffsetProvider codeGen) {
		String src = "";
		assert codeGen.getThisOffset() >= 0;

		Register reg = codeGen.getRegisterManager().acquire();
		ReturnRegister.setRegister(thisRef, reg);

		src += "   movl -" + codeGen.getThisOffset() + "(%ebp), %" + reg + "\n";

		return src;
	}
	
	public static String castExpression(CastExpression cast, CodeGeneratorInterface codeGen) throws BackendFailure {
		String src = "";
		src += codeGen.dispatch(cast.getExpr());

		// TODO: implement properly
		
		ReturnRegister.setRegister(cast, ReturnRegister.getRegister(cast.getExpr()));

		return src;
	}
	
	public static interface ThisOffsetProvider extends CodeGeneratorInterface {
		public int getThisOffset();
		public void setThisOffset(int offset);
	}
	
}
