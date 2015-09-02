package lfocc.features.x86.backend.generators;

import lfocc.features.classes.ast.CastExpression;
import lfocc.features.classes.ast.ClassDeclaration;
import lfocc.features.classes.ast.NewOperator;
import lfocc.features.classes.ast.ThisReference;
import lfocc.features.classes.ast.NullExpression;
import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.x86.backend.CodeGeneratorHelper;
import lfocc.features.x86.backend.CodeGeneratorInterface;
import lfocc.features.x86.backend.preparation.ClassPreparer.ClassTable;
import lfocc.features.x86.backend.preparation.ClassPreparer.InstanceTable;
import lfocc.framework.compiler.Backend.BackendFailure;

public class ClassCodeGenerator {

	private static final String CLASS_ESCAPE = "class__";
	
	public static String classDeclaration(ClassDeclaration classDecl, CodeGeneratorInterface codeGen) throws BackendFailure {
		
		String src = "\n\n";
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
		
		return src;
	}
	
	public static String getLabel(ClassDeclaration classDecl) {
		return CodeGeneratorHelper.escape(CLASS_ESCAPE + classDecl.getName());
	}
	
	public static String newOperator(NewOperator newOp) {
		String src = "";
		// TODO: implement
		return src;
	}
	
	public static String nullExpression(NullExpression nullExpr) {
		String src = "";
		// TODO: implement
		return src;
	}
	
	public static String thisReference(ThisReference thisRef) {
		String src = "";
		// TODO: implement
		return src;
	}
	
	public static String castExpression(CastExpression cast) {
		String src = "";
		// TODO: implement
		return src;
	}
}
