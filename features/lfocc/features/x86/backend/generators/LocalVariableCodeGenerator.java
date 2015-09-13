package lfocc.features.x86.backend.generators;

import lfocc.features.base.ast.ScopeKind;
import lfocc.features.variables.ast.Variable;
import lfocc.features.x86.backend.RegisterManager;
import lfocc.features.x86.backend.RegisterManager.Register;
import lfocc.features.x86.backend.preparation.FunctionPreparer.LocalVariableOffset;
import lfocc.features.x86.backend.CodeGeneratorHelper.ReturnRegister;

public class LocalVariableCodeGenerator {

	public static String localVariable(Variable variable, RegisterManager regs) {
		String src = "";
		assert variable.getDeclaration().extension(ScopeKind.class) == ScopeKind.LOCAL;
		
		Register reg = regs.acquire();
		ReturnRegister.setRegister(variable, reg);
		
		int offset = LocalVariableOffset.getOffset(variable.getDeclaration());
		src += "   movl -" + offset + "(%ebp), %" + reg + "\n";

		return src;
	}

	public static String localVariableAddress(Variable variable, RegisterManager regs) {
		String src = "";
		assert variable.getDeclaration().extension(ScopeKind.class) == ScopeKind.LOCAL;

		Register reg = regs.acquire();
		ReturnRegister.setRegister(variable, reg);

		int offset = LocalVariableOffset.getOffset(variable.getDeclaration());
		src += "   leal -" + offset + "(%ebp), %" + reg + "\n";

		return src;
	}
}