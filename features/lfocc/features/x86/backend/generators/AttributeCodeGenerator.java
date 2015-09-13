package lfocc.features.x86.backend.generators;

import lfocc.features.base.ast.ScopeKind;
import lfocc.features.variables.ast.Attribute;
import lfocc.features.variables.ast.Variable;
import lfocc.features.x86.backend.CodeGeneratorHelper.ReturnRegister;
import lfocc.features.x86.backend.CodeGeneratorInterface;
import lfocc.features.x86.backend.RegisterManager;
import lfocc.features.x86.backend.RegisterManager.Register;
import lfocc.features.x86.backend.generators.ClassCodeGenerator.ThisOffsetProvider;
import lfocc.framework.compiler.Backend.BackendFailure;

public class AttributeCodeGenerator {
	
	public static String attribute(Variable variable, ThisOffsetProvider codeGen) {
		String src = "";
		RegisterManager regs = codeGen.getRegisterManager();
		assert variable.getDeclaration().extension(ScopeKind.class) == ScopeKind.CLASS_MEMBER;

		// TODO
		
		Register reg = regs.acquire();
		ReturnRegister.setRegister(variable, reg);
		return src;
	}

	public static String attributeAddress(Variable variable, ThisOffsetProvider codeGen) {
		String src = "";
		RegisterManager regs = codeGen.getRegisterManager();
		assert variable.getDeclaration().extension(ScopeKind.class) == ScopeKind.CLASS_MEMBER;
		
		// TODO
		
		Register reg = regs.acquire();
		ReturnRegister.setRegister(variable, reg);
		return src;
	}

	public static String attribute(Attribute attribute, CodeGeneratorInterface codeGen) throws BackendFailure {
		String src = "";
		
		// TODO: implement properly
		src += codeGen.dispatch(attribute.getExpr());
		
		Register reg = ReturnRegister.getRegister(attribute.getExpr());
		ReturnRegister.setRegister(attribute, reg);
		return src;
	}

	public static String attributeAddress(Attribute attribute, CodeGeneratorInterface codeGen) throws BackendFailure {
		String src = "";

		// TODO: implement properly
		src += codeGen.dispatch(attribute.getExpr());
		
		Register reg = ReturnRegister.getRegister(attribute.getExpr());
		ReturnRegister.setRegister(attribute, reg);
		return src;
	}
}
