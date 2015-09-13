package lfocc.features.x86.backend.generators;

import lfocc.features.base.ast.ScopeKind;
import lfocc.features.variables.ast.Attribute;
import lfocc.features.variables.ast.Variable;
import lfocc.features.x86.backend.CodeGeneratorHelper.ReturnRegister;
import lfocc.features.x86.backend.CodeGeneratorInterface;
import lfocc.features.x86.backend.RegisterManager;
import lfocc.features.x86.backend.RegisterManager.Register;
import lfocc.features.x86.backend.generators.ClassCodeGenerator.ThisOffsetProvider;
import lfocc.features.x86.backend.preparation.ClassVariablePreparer.AttributeOffset;
import lfocc.framework.compiler.Backend.BackendFailure;

public class AttributeCodeGenerator {
	
	public static String attribute(Variable variable, ThisOffsetProvider codeGen) {
		String src = "";
		RegisterManager regs = codeGen.getRegisterManager();
		assert variable.getDeclaration().extension(ScopeKind.class) == ScopeKind.CLASS_MEMBER;
		assert codeGen.getThisOffset() >= 0;

		Register reg = regs.acquire();
		ReturnRegister.setRegister(variable, reg);

		src += "   movl " + codeGen.getThisOffset() + "(%ebp), %" + reg + "\n";
		src += "   movl " + AttributeOffset.getOffset(variable.getDeclaration()) + "(%" + reg + "), %" + reg + "\n";
		
		return src;
	}

	public static String attributeAddress(Variable variable, ThisOffsetProvider codeGen) {
		String src = "";
		RegisterManager regs = codeGen.getRegisterManager();
		assert variable.getDeclaration().extension(ScopeKind.class) == ScopeKind.CLASS_MEMBER;
		assert codeGen.getThisOffset() >= 0;
		
		Register reg = regs.acquire();
		ReturnRegister.setRegister(variable, reg);

		src += "   movl " + codeGen.getThisOffset() + "(%ebp), %" + reg + "\n";
		src += "   leal " + AttributeOffset.getOffset(variable.getDeclaration()) + "(%" + reg + "), %" + reg + "\n";

		return src;
	}

	public static String attribute(Attribute attribute, CodeGeneratorInterface codeGen) throws BackendFailure {
		String src = "";
		
		src += codeGen.dispatch(attribute.getExpr());
		
		Register reg = ReturnRegister.getRegister(attribute.getExpr());
		ReturnRegister.setRegister(attribute, reg);
		
		src += "   movl " + AttributeOffset.getOffset(attribute.getDeclaration()) + "(%" + reg + "), %" + reg + "\n";

		return src;
	}

	public static String attributeAddress(Attribute attribute, CodeGeneratorInterface codeGen) throws BackendFailure {
		String src = "";

		src += codeGen.dispatch(attribute.getExpr());
		
		Register reg = ReturnRegister.getRegister(attribute.getExpr());
		ReturnRegister.setRegister(attribute, reg);

		src += "   leal " + AttributeOffset.getOffset(attribute.getDeclaration()) + "(%" + reg + "), %" + reg + "\n";

		return src;
	}
}
