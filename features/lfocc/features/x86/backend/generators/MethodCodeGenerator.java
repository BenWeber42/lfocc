package lfocc.features.x86.backend.generators;

import lfocc.features.base.ast.ScopeKind;
import lfocc.features.functions.ast.FunctionCall;
import lfocc.features.x86.backend.CodeGeneratorHelper.ReturnRegister;
import lfocc.features.x86.backend.CodeGeneratorInterface;
import lfocc.features.x86.backend.RegisterManager;
import lfocc.features.x86.backend.RegisterManager.Register;

public class MethodCodeGenerator {

	public static String functionCall(FunctionCall call, CodeGeneratorInterface codeGen) {
		String src = "";
		RegisterManager regs = codeGen.getRegisterManager();
		
		assert call.getDeclaration().extension(ScopeKind.class) == ScopeKind.CLASS_MEMBER;
		
		Register reg = regs.acquire();
		ReturnRegister.setRegister(call, reg);
		
		if (!call.isExpression())
			regs.free(reg);

		return src;
	}
}
